package com.devloop.attachment.repository;

import com.devloop.attachment.entity.StudyAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SARepository extends JpaRepository<StudyAttachment, Long> {
}
