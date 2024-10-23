package com.devloop.party.response;

import com.devloop.party.entity.Party;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetPartyListResponse {
    private Long partyId;
    private String title;
    private String contents;

    private GetPartyListResponse(Long partyId,String title,String contents){
        this.partyId=partyId;
        this.title=title;
        this.contents=contents;
    }

    public static GetPartyListResponse from(Party party){
        return new GetPartyListResponse(
                party.getId(),
                party.getTitle(),
                party.getContents()
        );
    }
}
