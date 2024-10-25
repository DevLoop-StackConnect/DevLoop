package com.devloop.party.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SavePartyResponse {
    private Long partyId;
    private String title;
    private String contents;
    private String status;
    private String category;

    private SavePartyResponse(Long partyId,String title,String contents,String status,String category){
        this.partyId=partyId;
        this.title=title;
        this.contents=contents;
        this.status=status;
        this.category=category;
    }

    public static SavePartyResponse of(Long partyId,String title,String contents,String status,String category){
        return new SavePartyResponse(
                partyId,
                title,
                contents,
                status,
                category
        );
    }
}
