package com.devloop.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Objects;
import java.util.Properties;

@Slf4j
@Configuration
@EnableScheduling
public class CacheMonitoringConfig {
    private final MeterRegistry meterRegistry;
    private final RedisConnectionFactory redisConnectionFactory;

    public CacheMonitoringConfig(
            MeterRegistry meterRegistry,
            RedisConnectionFactory redisConnectionFactory) {
        this.meterRegistry = meterRegistry;
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @PostConstruct
    private void testRedisConnection() {
        try {
            redisConnectionFactory.getConnection().ping();
            log.info("Redis connection successful");
            initializeMetrics();
        } catch (Exception e) {
            log.error("Redis connection failed", e);
        }
    }

    private void initializeMetrics() {
        try {
            List<String> cacheNames = List.of("searchPreview", "searchDetail");
            cacheNames.forEach(this::registerCacheMetrics);
        } catch (Exception e) {
            log.error("캐시 메트릭 초기화 중 오류 발생", e);
        }
    }

    private void registerCacheMetrics(String cacheName) {
        try {
            List<Tag> tags = List.of(Tag.of("cache", cacheName));
            meterRegistry.gauge("cache.size", tags, this, value -> getCacheSize(cacheName));
            meterRegistry.gauge("cache.hits", tags, this, value -> getCacheHits(cacheName));
            meterRegistry.gauge("cache.misses", tags, this, value -> getCacheMisses(cacheName));
        } catch (Exception e) {
            log.error("캐시 메트릭 등록 중 오류 발생 - cache: {}", cacheName, e);
        }
    }

    @Scheduled(fixedRate = 60000)
    public void updateMetrics() {
        try {
            List<String> cacheNames = List.of("searchPreview", "searchDetail");
            cacheNames.forEach(this::updateCacheMetrics);
        } catch (Exception e) {
            log.error("캐시 메트릭 업데이트 중 오류 발생", e);
        }
    }

    private void updateCacheMetrics(String cacheName) {
        List<Tag> tags = List.of(Tag.of("cache", cacheName));
        double hitRate = calculateHitRate(getCacheHits(cacheName), getCacheMisses(cacheName));
        meterRegistry.gauge("cache.hit.rate", tags, hitRate);
        meterRegistry.gauge("cache.memory.used", tags, getMemoryUsage());

        log.info("Cache '{}' metrics updated - Hit Rate: {}, Memory Used: {}MB",
                cacheName, hitRate, getMemoryUsage());
    }

    private double calculateHitRate(long hits, long misses) {
        long total = hits + misses;
        return total == 0 ? 0.0 : (double) hits / total * 100;
    }

    private Properties fetchStatsInfo() {
        try {
            return Objects.requireNonNull(redisConnectionFactory.getConnection())
                    .serverCommands()
                    .info("stats");
        } catch (Exception e) {
            log.error("Redis 통계 정보 가져오기 실패", e);
            return new Properties();
        }
    }

    private Long getCacheHits(String cacheName) {
        String hits = fetchStatsInfo().getProperty("keyspace_hits");
        return hits != null ? Long.parseLong(hits) : 0L;
    }

    private Long getCacheMisses(String cacheName) {
        String misses = fetchStatsInfo().getProperty("keyspace_misses");
        return misses != null ? Long.parseLong(misses) : 0L;
    }

    private Long getCacheSize(String cacheName) {
        try {
            return Objects.requireNonNull(redisConnectionFactory.getConnection())
                    .serverCommands()
                    .dbSize();
        } catch (Exception e) {
            log.error("캐시 크기 조회 중 오류 발생 - cache: {}", cacheName, e);
            return 0L;
        }
    }

    private Double getMemoryUsage() {
        try {
            Properties info = Objects.requireNonNull(redisConnectionFactory.getConnection())
                    .serverCommands()
                    .info("memory");
            String memoryUsed = info.getProperty("used_memory");
            return memoryUsed != null ? Double.parseDouble(memoryUsed) / (1024 * 1024) : 0.0;
        } catch (Exception e) {
            log.error("메모리 사용량 조회 중 오류 발생", e);
            return 0.0;
        }
    }
}