package com.devloop.communitycomment.repository;

import com.devloop.communitycomment.entity.CommunityComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityCommentRepository extends JpaRepository<CommunityComment,Long> {
    //게시글 ID로 그 게시글에 속한 댓글 조회
    List<CommunityComment> findByCommunityId(Long communityId);
}
