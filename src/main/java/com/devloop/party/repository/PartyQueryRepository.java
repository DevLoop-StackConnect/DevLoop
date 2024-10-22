package com.devloop.party.repository;

import com.devloop.party.response.GetPartyListResponse;
import org.springframework.data.domain.PageRequest;

public interface PartyQueryRepository {

    GetPartyListResponse findPartyList(String title, String contents, PageRequest pageable);
}
