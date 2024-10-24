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
    private String status;
    private String category;

    private GetPartyListResponse(Long partyId,String title,String contents,String status,String category){
        this.partyId=partyId;
        this.title=title;
        this.contents=contents;
        this.status=status;
        this.category=category;
    }

    public static GetPartyListResponse from(Party party){

        return new GetPartyListResponse(
                party.getId(),
                party.getTitle(),
                party.getContents(),
                party.getStatus().getStatus(),
                party.getCategory().getDescription()
        );
    }
}
