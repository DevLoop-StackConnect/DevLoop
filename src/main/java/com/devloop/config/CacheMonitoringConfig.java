package com.devloop.config;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
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
    /**\
     * 메트릭을 저장하고 관리하기 위한 레지스트리
     * 메트릭이란 ? 시스템의 상태와 성능을 모니터링하기 위한 수치화된 측정값
     */
    private final MeterRegistry meterRegistry;
    //REdis 연결 관리 팩토리
    private final RedisConnectionFactory redisConnectionFactory;
    //생성자 주입으로 의존성 주입
    public CacheMonitoringConfig(
            MeterRegistry meterRegistry,
            RedisConnectionFactory redisConnectionFactory) {
        this.meterRegistry = meterRegistry;
        this.redisConnectionFactory = redisConnectionFactory;
        //객체 생성 시 메트릭 초기화
        initializeMetrics();
    }
    //메트릭 초기화 메서드
    private void initializeMetrics() {
        //모니터링할 캐시 이름 목록
        List<String> cacheNames = List.of("searchPreview", "searchDetail");
        //각 캐시에 메트릭 등록
        cacheNames.forEach(this::registerCacheMetrics);
    }
    //개별 캐시에 대한 메트릭 등록
    private void registerCacheMetrics(String cacheName) {
        //캐시 이름으로 태그 생성
        List<Tag> tags = List.of(Tag.of("cache", cacheName));
        //기본 캐시 메트릭 등록 : 크기 : 히트 수 : 미스 수
        meterRegistry.gauge("cache.size", tags, getCacheSize(cacheName));
        meterRegistry.gauge("cache.hits", tags, getCacheHits(cacheName));
        meterRegistry.gauge("cache.misses", tags, getCacheMisses(cacheName));
    }

    @Scheduled(fixedRate = 60000) // 1분마다 업데이트
    public void updateMetrics() {
        try {
            //모니터링할 캐시 목록
            List<String> cacheNames = List.of("searchPreview", "searchDetail");
            //각 캐시의 메트릭 업데이트
            cacheNames.forEach(this::updateCacheMetrics);
        } catch (Exception e) {
            log.error("캐시 메트릭 업데이트 중 오류 발생", e);
        }
    }
    //개별 캐시의 메트릭 업데이트
    private void updateCacheMetrics(String cacheName) {
        //캐시 태그 생성
        List<Tag> tags = List.of(Tag.of("cache", cacheName));
        //히트율 계산
        double hitRate = calculateHitRate(getCacheHits(cacheName), getCacheMisses(cacheName));
        //히트율과 메모리 사용량 메트릭 업데이트
        meterRegistry.gauge("cache.hit.rate", tags, hitRate);
        meterRegistry.gauge("cache.memory.used", tags, getMemoryUsage());

        log.info("Cache '{}' metrics updated - Hit Rate: {}, Memory Used: {}MB",
                cacheName, hitRate, getMemoryUsage());
    }
    //히트율 계산 메서드
    private double calculateHitRate(long hits, long misses) {
        long total = hits + misses;
        //총 요청이 0인경우 0% 반환, 그 외에는 히트율 계산
        return total == 0 ? 0.0 : (double) hits / total * 100;
    }
    //Redis 통계 정보 조회
    private Properties fetchStatsInfo() {
        try {
            //Redis 연결을 통해 status 정보 조회
            return Objects.requireNonNull(redisConnectionFactory.getConnection())
                    .serverCommands()
                    .info("stats");
        } catch (Exception e) {
            log.error("Redis 통계 정보 가져오기 실패", e);
            return new Properties();
        }
    }
    //캐시 히트 수 조회
    private Long getCacheHits(String cacheName) {
        //status 정보에서 키스페이스 히트 수 출력
        String hits = fetchStatsInfo().getProperty("keyspace_hits");
        return hits != null ? Long.parseLong(hits) : 0L;
    }
    //캐시 미스 수 조회
    private Long getCacheMisses(String cacheName) {
        //status 정보에서 키스페이스 미스 수 출력
        String misses = fetchStatsInfo().getProperty("keyspace_misses");
        return misses != null ? Long.parseLong(misses) : 0L;
    }
    //캐시 크기 조회
    private Long getCacheSize(String cacheName) {
        try {
            //Redis 데이터베이스 크기 조회
            return Objects.requireNonNull(redisConnectionFactory.getConnection())
                    .serverCommands()
                    .dbSize();
        } catch (Exception e) {
            log.error("캐시 크기 조회 중 오류 발생 - cache: {}", cacheName, e);
            return 0L;
        }
    }
    //메모리 사용량 조회
    private Double getMemoryUsage() {
        try {
            Properties info = Objects.requireNonNull(redisConnectionFactory.getConnection())
                    .serverCommands()
                    .info("memory");
            String memoryUsed = info.getProperty("used_memory");
            return memoryUsed != null ? Double.parseDouble(memoryUsed) / (1024 * 1024) : 0.0; // MB 단위로 변환
        } catch (Exception e) {
            log.error("메모리 사용량 조회 중 오류 발생", e);
            return 0.0;
        }
    }
}