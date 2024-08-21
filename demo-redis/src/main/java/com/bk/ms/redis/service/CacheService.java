package com.bk.ms.redis.service;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheService {

    private final CacheManager cacheManager;

    private final RedisTemplate<String, String> redisTemplate;
    @Resource(name="redisTemplate")
    private ListOperations<String, String> listOps;

    public void evictCacheValues(String cacheName) {
        Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
        log.info("All values evicted for cache {}", cacheName);
//        redisTemplate.opsForZSet().
    }
}