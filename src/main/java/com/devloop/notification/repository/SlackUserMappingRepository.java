package com.devloop.notification.repository;

import com.devloop.notification.entity.SlackUserMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SlackUserMappingRepository extends JpaRepository<SlackUserMapping, Long> {

    Optional<SlackUserMapping> findByUserIdAndActiveTrue(Long userId);
    Optional<SlackUserMapping> findBySlackId(String slackId);
    Optional<SlackUserMapping> findBySlackEmail(String slackEmail);

    @Query("SELECT m FROM SlackUserMapping m WHERE m.active = true")
    List<SlackUserMapping> findAllActive();

    boolean existsBySlackEmail(String email);

    @Query("SELECT m FROM SlackUserMapping m WHERE m.user.id = :userId " +
            "ORDER BY m.createdAt DESC")
    List<SlackUserMapping> findAllByUserIdOrderByCreatedAtDesc(
            @Param("userId") Long userId
    );

    @Modifying
    @Query("UPDATE SlackUserMapping m SET m.active = false WHERE m.user.id = :userId")
    void deactivateAllByUserId(@Param("userId") Long userId);
}