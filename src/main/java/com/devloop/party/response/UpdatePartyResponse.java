package com.devloop.party.response;

import com.devloop.party.entity.Party;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdatePartyResponse {
    private Long partyId;
    private String title;
    private String contents;

    private UpdatePartyResponse(Long partyId,String title,String contents){
        this.partyId=partyId;
        this.title=title;
        this.contents=contents;
    }

    public static UpdatePartyResponse from(Party party){
        return new UpdatePartyResponse(
                party.getId(),
                party.getTitle(),
                party.getContents()
        );
    }
}
