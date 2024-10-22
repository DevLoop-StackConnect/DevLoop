package com.devloop.communitycomment.repository;

import com.devloop.communitycomment.entity.CommunityComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityCommentRepository extends JpaRepository<CommunityComment,Long> {
}
