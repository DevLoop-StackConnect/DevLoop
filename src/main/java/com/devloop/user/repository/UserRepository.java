package com.devloop.user.repository;

import com.devloop.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(@NotBlank(message = "이메일은 필수 입력사항입니다.") @Email(message = "이메일 형식을 맞춰주세요.") String email);
}
