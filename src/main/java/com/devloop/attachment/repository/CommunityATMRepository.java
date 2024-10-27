package com.devloop.attachment.repository;

import com.devloop.attachment.entity.CommunityAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommunityATMRepository extends JpaRepository<CommunityAttachment, Long> {
    Optional<CommunityAttachment> findByCommunityId(Long communityId);
}
