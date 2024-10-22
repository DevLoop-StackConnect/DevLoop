package com.devloop.party.service;

import com.devloop.common.AuthUser;
import com.devloop.party.entity.Party;
import com.devloop.party.repository.PartyRepository;
import com.devloop.party.request.SavePartyRequest;
import com.devloop.party.response.SavePartyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
@Transactional
@RequiredArgsConstructor
public class PartyService {

    private final PartyRepository partyRepository;


    public SavePartyResponse saveParty(SavePartyRequest savePartyRequest) {

        //유저가 존재하는 지 확인

        Party newParty=Party.from(savePartyRequest);
        partyRepository.save(newParty);

        return SavePartyResponse.from(newParty);
    }
}
