package com.devloop.communitycomment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.devloop.communitycomment.entity.CommunityComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {
    //게시글 ID로 그 게시글에 속한 댓글 조회
    Page<CommunityComment> findByCommunityId(Long communityId, Pageable pageable);
}
