package com.devloop.party.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class GetPartyDetailResponse {
    private Long partyId;
    private String title;
    private String contents;
    private String status;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String imageUrl;

    private GetPartyDetailResponse(Long partyId,String title,String contents,String status,String category, LocalDateTime createdAt, LocalDateTime modifiedAt, String imageUrl){
        this.partyId=partyId;
        this.title=title;
        this.contents=contents;
        this.status=status;
        this.category=category;
        this.createdAt=createdAt;
        this.modifiedAt=modifiedAt;
        this.imageUrl=imageUrl;
    }

    public static GetPartyDetailResponse of(Long partyId,String title,String contents,String status,String category, LocalDateTime createdAt, LocalDateTime modifiedAt,String imageUrl){
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
