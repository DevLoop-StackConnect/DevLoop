package com.devloop.party.response;

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

    public static GetPartyListResponse of(Long partyId,String title,String contents,String status,String category){
        return new GetPartyListResponse(
                partyId,
                title,
                contents,
                status,
                category
        );
    }
}
