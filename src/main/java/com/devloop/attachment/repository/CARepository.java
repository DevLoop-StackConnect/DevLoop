package com.devloop.attachment.repository;

import com.devloop.attachment.entity.CommunityAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CARepository extends JpaRepository<CommunityAttachment, Long> {
}
