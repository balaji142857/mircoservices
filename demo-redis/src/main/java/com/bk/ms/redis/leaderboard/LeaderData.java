package com.bk.ms.redis.leaderboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class LeaderData {

    private String _member;
    private Double _score;
    private Long _rank;

}
