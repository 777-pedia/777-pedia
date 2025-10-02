package org.example.pedia_777.common.config;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class CacheConfig {

    // 캐시 스탬피드 방지를 위한 기본 지터 추가
    public static Duration addDefaultJitter(Duration baseDuration) {

        long bound = (long) (baseDuration.getSeconds() * 0.1);

        if (bound <= 0) {
            return baseDuration;
        }

        long jitterSeconds = ThreadLocalRandom.current().nextLong(bound);
        return baseDuration.plusSeconds(jitterSeconds);
    }

    @Bean("redisCacheManager")
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer()))
                .entryTtl(Duration.ofMinutes(10));

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(defaultConfig)

                .withCacheConfiguration(
                        CacheType.NAME_LIST_POPULAR_KEYWORDS, // "popularKeywords"
                        defaultConfig.entryTtl(addDefaultJitter(CacheType.LIST_POPULAR_KEYWORDS.getTtl()))
                )
                .withCacheConfiguration(
                        CacheType.NAME_MOVIE_SEARCH_CURRENT_POPULAR, // "movieSearchCurrentPopular"
                        defaultConfig.entryTtl(addDefaultJitter(CacheType.MOVIE_SEARCH_CURRENT_POPULAR.getTtl()))
                )
                .withCacheConfiguration(
                        CacheType.NAME_MOVIE_SEARCH_PREV_POPULAR, // "movieSearchPrevPopular"
                        defaultConfig.entryTtl(addDefaultJitter(CacheType.MOVIE_SEARCH_PREV_POPULAR.getTtl()))
                )
                .withCacheConfiguration(
                        CacheType.NAME_LIST_DAILY_TOP_10_MOVIES,
                        defaultConfig.entryTtl(addDefaultJitter(CacheType.LIST_DAILY_TOP_10_MOVIES.getTtl()))
                )
                .withCacheConfiguration(
                        CacheType.NAME_LIST_WEEKLY_TOP_10_MOVIES,
                        defaultConfig.entryTtl(addDefaultJitter(CacheType.LIST_WEEKLY_TOP_10_MOVIES.getTtl()))
                )
                .withCacheConfiguration(
                        CacheType.NAME_MOVIE_DETAILS_TOP_10,
                        defaultConfig.entryTtl(addDefaultJitter(CacheType.MOVIE_DETAILS_TOP_10.getTtl()))
                )
                .build();
    }
}
