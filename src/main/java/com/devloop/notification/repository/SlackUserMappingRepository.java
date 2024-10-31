package com.devloop.notification.repository;

import com.devloop.notification.entity.SlackUserMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SlackUserMappingRepository extends JpaRepository<SlackUserMapping, Long> {
    //특정 사용자 ID로 활성화된 Slack 매핑 정보를 찾음
    Optional<SlackUserMapping> findByUserIdAndActiveTrue(Long userId);

    @Modifying // 데이터 수정 작업을 수행하는 쿼리
    // 사용자 ID에 해당하는 모든 Slack 매핑을 비활성화
    @Query("UPDATE SlackUserMapping m SET m.active = false WHERE m.user.id = :userId")
    void deactivateAllByUserId(@Param("userId") Long userId);
}