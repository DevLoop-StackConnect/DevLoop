package com.devloop.party.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GetPartyDetailResponse {
    private final Long partyId;
    private final String title;
    private final String contents;
    private final String status;
    private final String category;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
    private final String imageUrl;

    private GetPartyDetailResponse(Long partyId, String title, String contents, String status, String category, LocalDateTime createdAt, LocalDateTime modifiedAt, String imageUrl) {
        this.partyId = partyId;
        this.title = title;
        this.contents = contents;
        this.status = status;
        this.category = category;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.imageUrl = imageUrl;
    }

    public static GetPartyDetailResponse of(Long partyId, String title, String contents, String status, String category, LocalDateTime createdAt, LocalDateTime modifiedAt, String imageUrl) {
        return new GetPartyDetailResponse(
                partyId,
                title,
                contents,
                status,
                category,
                createdAt,
                modifiedAt,
                imageUrl
        );
    }
}
