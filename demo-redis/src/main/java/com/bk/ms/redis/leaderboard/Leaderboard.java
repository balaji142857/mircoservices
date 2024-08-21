package com.bk.ms.redis.leaderboard;

import java.util.*;

import lombok.AllArgsConstructor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.Transaction;

@AllArgsConstructor
public class Leaderboard {

    public static final int DEFAULT_PAGE_SIZE = 25;
    public static final List<LeaderData> EMPTY_LEADER_DATA = Collections.emptyList();

    private Jedis _jedis;
    private int _pageSize;


      public void setPageSize(int pageSize) {
        if (pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        _pageSize = pageSize;
    }


    public void disconnect() {
        _jedis.disconnect();
    }

    public long totalMembersIn(String leaderboardName) {
        return _jedis.zcard(leaderboardName);
    }


    public int totalPagesIn(String leaderboardName, Integer pageSize) {
        if (pageSize == null) {
            pageSize = _pageSize;
        }
        return (int) Math.ceil((float) totalMembersIn(leaderboardName) / (float) pageSize);
    }

    public long totalMembersInScoreRangeIn(String leaderboardName, double minScore, double maxScore) {
        return _jedis.zcount(leaderboardName, minScore, maxScore);
    }


    public long rankMemberIn(String leaderboardName, String member, double score) {
        return _jedis.zadd(leaderboardName, score, member);
    }


    public Double scoreForIn(String leaderboardName, String member) {
        return _jedis.zscore(leaderboardName, member);
    }


    public double changeScoreForMemberIn(String leaderboardName, String member, double delta) {
        return _jedis.zincrby(leaderboardName, delta, member);
    }


    public boolean checkMemberIn(String leaderboardName, String member) {
        return _jedis.zscore(leaderboardName, member) != null;
    }


    public Long rankForIn(String leaderboardName, String member, boolean useZeroIndexForRank) {

        Long result = null;

        Long redisRank = _jedis.zrevrank(leaderboardName, member);

        if (redisRank != null) {
            if (useZeroIndexForRank) {
                result = redisRank;
            } else {
                result = (redisRank + 1);
            }
        }

        return result;
    }


    public long removeMembersInScoreRangeIn(String leaderboardName, double minScore, double maxScore) {
        return _jedis.zremrangeByScore(leaderboardName, minScore, maxScore);
    }


    public Map<String, Object> scoreAndRankForIn(String leaderboardName, String member, boolean useZeroIndexForRank) {
        HashMap<String, Object> data = new HashMap<>();

        Transaction transaction = _jedis.multi();
        transaction.zscore(leaderboardName, member);
        transaction.zrevrank(leaderboardName, member);
        List<Object> response = transaction.exec();

        data.put("member", member);
        data.put("score", response.get(0));
        if (useZeroIndexForRank) {
            data.put("rank", response.get(1));
        } else {
            data.put("rank", (Long) response.get(1) + 1);
        }

        return data;
    }


    public List<LeaderData> leadersIn(String leaderboardName, int currentPage, boolean useZeroIndexForRank, int pageSize) {
        if (currentPage < 1) {
            currentPage = 1;
        }

        if (pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        if (currentPage > totalPagesIn(leaderboardName, pageSize)) {
            currentPage = totalPagesIn(leaderboardName, pageSize);
        }

        int indexForRedis = currentPage - 1;
        int startingOffset = indexForRedis * pageSize;
        if (startingOffset < 0) {
            startingOffset = 0;
        }
        int endingOffset = (startingOffset + pageSize) - 1;

        Set<Tuple> rawLeaderData = _jedis.zrevrangeWithScores(leaderboardName, startingOffset, endingOffset);
        return massageLeaderData(leaderboardName, rawLeaderData, useZeroIndexForRank);
    }


    public List<LeaderData> aroundMeIn(String leaderboardName, String member, boolean useZeroIndexForRank, int pageSize) {
        Long reverseRankForMember = _jedis.zrevrank(leaderboardName, member);

        if (reverseRankForMember == null) {
            return EMPTY_LEADER_DATA;
        }


        if (pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        int startingOffset = reverseRankForMember.intValue() - (pageSize / 2);
        if (startingOffset < 0) {
            startingOffset = 0;
        }
        int endingOffset = (startingOffset + pageSize) - 1;

        Set<Tuple> rawLeaderData = _jedis.zrevrangeWithScores(leaderboardName, startingOffset, endingOffset);
        return massageLeaderData(leaderboardName, rawLeaderData, useZeroIndexForRank);
    }


    public List<LeaderData> rankedInListIn(String leaderboardName, List<String> members, boolean useZeroIndexForRank) {
        List<LeaderData> leaderData = new ArrayList<>();

        for (String member : members) {
            Double score = scoreForIn(leaderboardName, member);

            if (score != null) {

                long rank = rankForIn(leaderboardName, member, useZeroIndexForRank);
                LeaderData memberData = new LeaderData(member, score, rank);
                leaderData.add(memberData);
            }
        }

        return leaderData;
    }


    private List<LeaderData> massageLeaderData(String leaderboardName, Set<Tuple> memberData, boolean useZeroIndexForRank) {
        List<LeaderData> leaderData = new ArrayList<>();

        for (Tuple memberDataTuple : memberData) {
            LeaderData leaderDataItem = new LeaderData(memberDataTuple.getElement(), memberDataTuple.getScore(), rankForIn(leaderboardName, memberDataTuple.getElement(), useZeroIndexForRank));
            leaderData.add(leaderDataItem);
        }

        return leaderData;
    }
}