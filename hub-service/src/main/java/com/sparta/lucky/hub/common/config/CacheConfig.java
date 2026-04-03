package com.sparta.lucky.hub.common.config;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sparta.lucky.hub.application.dto.GetHubResult;
import com.sparta.lucky.hub.domain.HubRoute;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.List;

@Configuration
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        RedisSerializationContext.SerializationPair<String> keySerializer =
                RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer());

        // hub 캐시: GetHubResult 단건
        RedisCacheConfiguration hubConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(120))
                .serializeKeysWith(keySerializer)
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new Jackson2JsonRedisSerializer<>(objectMapper, GetHubResult.class)
                ));

        // hubs 캐시: List<GetHubResult>
        JavaType hubsType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, GetHubResult.class);
        RedisCacheConfiguration hubsConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(120))
                .serializeKeysWith(keySerializer)
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new Jackson2JsonRedisSerializer<>(objectMapper, hubsType)
                ));

        // routes 캐시: List<HubRoute>
        JavaType routesType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, HubRoute.class);
        RedisCacheConfiguration routesConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .serializeKeysWith(keySerializer)
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new Jackson2JsonRedisSerializer<>(objectMapper, routesType)
                ));

        return RedisCacheManager.builder(connectionFactory)
                .withCacheConfiguration("hub", hubConfig)
                .withCacheConfiguration("hubs", hubsConfig)
                .withCacheConfiguration("routes", routesConfig)
                .build();
    }
}