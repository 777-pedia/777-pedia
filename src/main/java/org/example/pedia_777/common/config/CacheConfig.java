package org.example.pedia_777.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class CacheConfig {

    @Primary
    @Bean("caffeineCacheManager")
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        cacheManager.registerCustomCache(CacheType.IS_POPULAR_KEYWORD_NAME, // 키워드의 인기 검색어 여부
                Caffeine.newBuilder()
                        .expireAfterWrite(CacheType.IS_POPULAR_KEYWORD.getTtl().toMinutes(), TimeUnit.MINUTES)
                        .maximumSize(1000)
                        .build());

        return cacheManager;
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
                        CacheType.POPULAR_KEYWORDS_NAME, // "popularKeywords"
                        defaultConfig.entryTtl(CacheType.POPULAR_KEYWORDS.getTtl())
                )
                .withCacheConfiguration(
                        CacheType.MOVIE_SEARCH.getCacheName(), // "movieSearch"
                        defaultConfig.entryTtl(CacheType.MOVIE_SEARCH.getTtl())
                )
                .build();
    }
}
