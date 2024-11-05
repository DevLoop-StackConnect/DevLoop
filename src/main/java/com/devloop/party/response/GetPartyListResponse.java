package com.devloop.party.response;

import lombok.Getter;

@Getter
public class GetPartyListResponse {
    private final Long partyId;
    private final String title;
    private final String contents;
    private final String status;
    private final String category;

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
