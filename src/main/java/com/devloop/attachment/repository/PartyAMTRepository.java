package com.devloop.attachment.repository;

import com.devloop.attachment.entity.PartyAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartyAMTRepository extends JpaRepository<PartyAttachment, Long> {

    Optional<PartyAttachment> findByPartyId(Long partyId);
}
