package com.devloop.attachment.service;

import com.devloop.attachment.entity.PartyAttachment;
import com.devloop.attachment.repository.CommunityATMRepository;
import com.devloop.attachment.repository.PartyAMTRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PartyAttachmentService {
    private final PartyAMTRepository partyAMTRepository;

    public Optional<PartyAttachment> findPartyAttachmentByPartyId(Long id){
        return partyAMTRepository.findByPartyId(id);
    }

    public void deletePartyAttachment(PartyAttachment partyAttachment){
        partyAMTRepository.delete(partyAttachment);
    }

    public PartyAMTRepository getCommunityATMRepository() {
        return partyAMTRepository;
    }
}
