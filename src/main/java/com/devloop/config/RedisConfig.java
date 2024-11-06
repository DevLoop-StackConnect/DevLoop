package com.devloop.config;

import com.devloop.notification.dto.NotificationMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RedisConfig {

    //Redis에서 사용할 ObjectMapper Bean 설정
    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        //Java8 시간 타입 처리를 위한 모듈 등록
        objectMapper.registerModule(new JavaTimeModule());
        // 날짜를 타임 스탬프로 변환하지 않도록 설정
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }
    //RedisTemplate 생성을 위한 공통 메서드
    private <T> RedisTemplate<String, T> createRedisTemplate(
            RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        //Redis 연결 설정
        template.setConnectionFactory(connectionFactory);
        // Json 직렬화를 위한 serializer
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        //키는 문자열로, 값을 Json으로 직렬화하도록 설정
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        //트랙잭션 지원 활성화
        template.setEnableTransactionSupport(true);
        //설정 젹용
        template.afterPropertiesSet();
        return template;
    }
    //일반적인 Object를 저장하기 위한 RedisTemplate Bean
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper redisObjectMapper) {
        return createRedisTemplate(connectionFactory, redisObjectMapper);
    }
    //알림 전용 RedisTemplate
    @Bean
    public RedisTemplate<String, NotificationMessage> notificationRedisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper redisObjectMapper) {
        return createRedisTemplate(connectionFactory, redisObjectMapper);
    }
    //Redis 캐시 매니저
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        //기본 캐시 설정
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1)) //캐시 유효시간
                .disableCachingNullValues() //null 값 캐싱 비활성화
                 .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer())) //키 직렬화
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer())); //값 직렬화
        //각 캐시별 개별 설정
        Map<String, RedisCacheConfiguration> configurations = new HashMap<>();
        //검색 미리보기 캐시 설정
        configurations.put("searchPreview", RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .disableCachingNullValues());
        //검색 상세검색 캐시 설정
        configurations.put("searchDetail", RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .disableCachingNullValues());
        //캐시 매니저 생성 및 설정
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(configurations)
                .build();
    }

    @Bean
    //랭킹 캐시를 위한 문자열 전용 RedisTemplate
    public RedisTemplate<String, String> rankingRedisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        //키와 벨류 모두 문자열로 직렬화
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
}