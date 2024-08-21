package com.bk.ms.redis.web;

import com.bk.ms.redis.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.bk.ms.redis.util.Constants.CACHE_NAME_COUNTRIES;

@Profile("!prod")
@RestController
@RequiredArgsConstructor
@RequestMapping("/cache")
public class CacheController {

    private final CacheService cacheService;

    @DeleteMapping("/evict")
    public String evict() {
        cacheService.evictCacheValues(CACHE_NAME_COUNTRIES);
        return "OK";
    }
}