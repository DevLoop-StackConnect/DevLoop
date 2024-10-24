package com.devloop.community.dto.response;

import com.devloop.common.enums.Category;
import com.devloop.community.entity.Community;
import com.devloop.community.entity.ResolveStatus;
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

    private CommunityDetailResponse(Long communityId, String title, String content, String status, String category, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.communityId = communityId;
        this.title = title;
        this.content = content;
        this.status = status;
        this.category = category;
        this.createdAt=createdAt;
        this.modifiedAt=modifiedAt;
    }

    //코드컨벤션에 맞춰서 정적 팩토리 메서드 추가
    public static CommunityDetailResponse from(Community community) {
        return new CommunityDetailResponse(
                community.getId(),
                community.getTitle(),
                community.getContent(),
                community.getResolveStatus().getDescription(),
                community.getCategory().getDescription(),
                community.getCreatedAt(),
                community.getModifiedAt()
        );
    }
}
