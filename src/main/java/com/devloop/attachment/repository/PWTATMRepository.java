package com.devloop.attachment.repository;

import com.devloop.attachment.entity.PWTAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PWTATMRepository extends JpaRepository<PWTAttachment, Long> {
    Optional<PWTAttachment> findByPWTId(Long id);
}
