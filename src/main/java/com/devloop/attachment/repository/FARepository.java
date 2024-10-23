package com.devloop.attachment.repository;

import com.devloop.attachment.entity.ProfileAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FARepository extends JpaRepository<ProfileAttachment, Long> {
}
