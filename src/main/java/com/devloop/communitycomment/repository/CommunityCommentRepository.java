package com.devloop.communitycomment.repository;

import com.devloop.communitycomment.entity.CommunityComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {
    //게시글 ID로 그 게시글에 속한 댓글 조회
    Page<CommunityComment> findByCommunityId(Long communityId, Pageable pageable);

    @Query("SELECT c FROM CommunityComment c JOIN FETCH c.community cm " +
            "JOIN FETCH cm.user u WHERE c.id = :commentId")
    Optional<CommunityComment> findByIdWithCommunityAndUser(
            @Param("commentId") Long commentId
    );

    @Query("SELECT c FROM CommunityComment c WHERE c.community.user.id = :userId " +
            "AND c.createdAt >= :since ORDER BY c.createdAt DESC")
    List<CommunityComment> findRecentCommentsByPostAuthor(
            @Param("userId") Long userId,
            @Param("since") LocalDateTime since
    );
}
