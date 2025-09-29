package org.example.pedia_777.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {

        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        cacheManager.registerCustomCache(CacheType.MOVIE_SEARCH_NAME,
                Caffeine.newBuilder()
                        .recordStats()
                        .expireAfterWrite(CacheType.MOVIE_SEARCH.getExpiredAfterWrite(), TimeUnit.MINUTES)
                        .maximumSize(CacheType.MOVIE_SEARCH.getMaximumSize())
                        .build());

        return cacheManager;
    }
}
