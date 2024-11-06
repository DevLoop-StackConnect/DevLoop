package com.devloop.attachment.service;

import com.devloop.attachment.entity.PartyAttachment;
import com.devloop.attachment.repository.PartyAMTRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartyAttachmentService {
    private final PartyAMTRepository partyAMTRepository;

    public Optional<PartyAttachment> findPartyAttachmentByPartyId(Long id) {
        return partyAMTRepository.findByPartyId(id);
    }

    @Transactional
    public void deletePartyAttachment(PartyAttachment partyAttachment) {
        partyAMTRepository.delete(partyAttachment);
    }

    @Transactional
    public void savePartyAttachment(PartyAttachment partyAttachment) {
        partyAMTRepository.save(partyAttachment);
    }
}
