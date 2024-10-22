package com.devloop.party.response;


import com.devloop.party.entity.Party;
import com.devloop.party.request.SavePartyRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SavePartyResponse {

    private Long partyId;
    private String title;
    private String contents;

    private SavePartyResponse(Long partyId,String title,String contents){
        this.partyId=partyId;
        this.title=title;
        this.contents=contents;
    }

    public static SavePartyResponse from(Party party){
        return new SavePartyResponse(
                party.getId(),
                party.getTitle(),
                party.getContents()
        );
    }
}
