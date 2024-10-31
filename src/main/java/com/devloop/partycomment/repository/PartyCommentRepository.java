package com.devloop.partycomment.repository;

import com.devloop.partycomment.entity.PartyComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PartyCommentRepository extends JpaRepository<PartyComment,Long> {
    Page<PartyComment> findByPartyId(Long id, Pageable pageable);

    @Query("SELECT pc FROM PartyComment pc JOIN FETCH pc.party p " +
            "JOIN FETCH p.user u WHERE pc.id = :commentId")
    Optional<PartyComment> findByIdWithPartyAndUser(
            @Param("commentId") Long commentId
    );

    @Query("SELECT pc FROM PartyComment pc WHERE pc.party.user.id = :userId " +
            "AND pc.createdAt >= :since ORDER BY pc.createdAt DESC")
    List<PartyComment> findRecentCommentsByPartyAuthor(
            @Param("userId") Long userId,
            @Param("since") LocalDateTime since
    );
}
