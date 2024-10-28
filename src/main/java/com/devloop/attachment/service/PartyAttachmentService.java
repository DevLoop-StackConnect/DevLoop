package com.devloop.attachment.service;

import com.devloop.attachment.entity.PartyAttachment;
import com.devloop.attachment.repository.PartyAMTRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartyAttachmentService {
    private final PartyAMTRepository partyAMTRepository;

    public PartyAttachment findPartyAttachmentByPartyId(Long id){
        return partyAMTRepository.findByPartyId(id)
                .orElse(null);
    }

    public void deletePartyAttachment(PartyAttachment partyAttachment){
        partyAMTRepository.delete(partyAttachment);
    }
}
