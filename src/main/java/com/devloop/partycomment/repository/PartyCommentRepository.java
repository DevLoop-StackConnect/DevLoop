package com.devloop.partycomment.repository;

import com.devloop.partycomment.entity.PartyComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyCommentRepository extends JpaRepository<PartyComment, Long> {
    Page<PartyComment> findByPartyId(Long id, Pageable pageable);
}
