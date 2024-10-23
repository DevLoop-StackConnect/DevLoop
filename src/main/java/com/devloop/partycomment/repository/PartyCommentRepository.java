package com.devloop.partycomment.repository;

import com.devloop.partycomment.entity.PartyComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PartyCommentRepository extends JpaRepository<PartyComment,Long> {
    List<PartyComment> findByPartyId(Long partyId);
}
