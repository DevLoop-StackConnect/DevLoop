package com.devloop.attachment.repository;

import com.devloop.attachment.entity.PartyAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PARepository extends JpaRepository<PartyAttachment, Long> {
}
