package com.devloop.attachment.repository;

import com.devloop.attachment.entity.PWTAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PWTATMRepository extends JpaRepository<PWTAttachment, Long> {
}
