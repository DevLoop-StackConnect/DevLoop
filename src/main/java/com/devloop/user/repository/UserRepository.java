package com.devloop.user.repository;

import com.devloop.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(@NotBlank(message = "이메일은 필수 입력사항입니다.") @Email(message = "이메일 형식을 맞춰주세요.") String email);
    Optional<User> findByKakaoId(Long kakaoId);

    // Slack 관련 메서드 추가
    Optional<User> findBySlackId(String slackId);
    Optional<User> findBySlackEmail(String slackEmail);
    List<User> findBySlackLinkedTrue();

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.loginId = :employeeId")
    Optional<User> findByEmailAndEmployeeId(
            @Param("email") String email,
            @Param("employeeId") String employeeId
    );
}
