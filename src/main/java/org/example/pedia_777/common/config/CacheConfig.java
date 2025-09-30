package org.example.pedia_777.common.config;

import java.time.Duration;
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
                        CacheType.NAME_POPULAR_KEYWORDS, // "popularKeywords"
                        defaultConfig.entryTtl(CacheType.POPULAR_KEYWORDS.getTtl())
                )
                .withCacheConfiguration(
                        CacheType.NAME_MOVIE_SEARCH_CURRENT_POPULAR, // "movieSearchCurrentPopular"
                        defaultConfig.entryTtl(CacheType.MOVIE_SEARCH_CURRENT_POPULAR.getTtl())
                )
                .withCacheConfiguration(
                        CacheType.NAME_MOVIE_SEARCH_PREV_POPULAR, // "movieSearchPrevPopular"
                        defaultConfig.entryTtl(CacheType.MOVIE_SEARCH_PREV_POPULAR.getTtl())
                )
                .build();
    }
}
