package com.devloop.party.response;

import com.devloop.party.entity.Party;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetPartyDetailResponse {
    private Long partyId;
    private String title;
    private String contents;

    private GetPartyDetailResponse(Long partyId,String title,String contents){
        this.partyId=partyId;
        this.title=title;
        this.contents=contents;
    }

    public static GetPartyDetailResponse from(Party party){
        return new GetPartyDetailResponse(
                party.getId(),
                party.getTitle(),
                party.getContents()
        );
    }
}
