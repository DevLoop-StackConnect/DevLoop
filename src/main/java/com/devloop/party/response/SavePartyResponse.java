package com.devloop.party.response;

import lombok.Getter;

@Getter
public class SavePartyResponse {
    private final Long partyId;
    private final String title;
    private final String contents;
    private final String status;
    private final String category;

    private SavePartyResponse(Long partyId, String title, String contents, String status, String category) {
        this.partyId = partyId;
        this.title = title;
        this.contents = contents;
        this.status = status;
        this.category = category;
    }

    public static SavePartyResponse of(Long partyId, String title, String contents, String status, String category) {
        return new SavePartyResponse(
                partyId,
                title,
                contents,
                status,
                category
        );
    }
}
