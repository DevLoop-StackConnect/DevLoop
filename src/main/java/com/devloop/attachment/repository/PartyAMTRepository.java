package com.devloop.attachment.repository;

import com.devloop.attachment.entity.PartyAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyAMTRepository extends JpaRepository<PartyAttachment, Long> {
}
