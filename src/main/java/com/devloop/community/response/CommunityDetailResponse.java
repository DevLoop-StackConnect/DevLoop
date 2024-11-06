package com.devloop.community.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommunityDetailResponse {
    private final Long communityId;
    private final String title;
    private final String content;
    private final String status;
    private final String category;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
    private final String imageUrl;

    private CommunityDetailResponse(Long communityId, String title, String content, String status, String category, LocalDateTime createdAt, LocalDateTime modifiedAt, String imageUrl) {
        this.communityId = communityId;
        this.title = title;
        this.content = content;
        this.status = status;
        this.category = category;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.imageUrl = imageUrl;
    }

    // 첨부파일 URL 포함한 메서드 (단건 조회용)
    public static CommunityDetailResponse withAttachment(Long communityId, String title, String content, String status, String category,
                                                         LocalDateTime createdAt, LocalDateTime modifiedAt, String imageUrl) {
        return new CommunityDetailResponse(
                communityId,
                title,
                content,
                status,
                category,
                createdAt,
                modifiedAt,
                imageUrl
        );
    }

    // 첨부파일 URL 미포함 메서드 (업데이트 응답용)
    public static CommunityDetailResponse withoutAttachment(Long communityId, String title, String content, String status, String category,
                                                            LocalDateTime createdAt, LocalDateTime modifiedAt) {
        return new CommunityDetailResponse(
                communityId,
                title,
                content,
                status,
                category,
                createdAt,
                modifiedAt,
                null
        );
    }
}
