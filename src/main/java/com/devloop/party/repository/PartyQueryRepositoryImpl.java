package com.devloop.party.repository;


import com.devloop.party.response.GetPartyListResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
public class PartyQueryRepositoryImpl implements PartyQueryRepository{


    @Override
    public GetPartyListResponse findPartyList(String title, String contents, PageRequest pageable) {
        return null;
    }
}
