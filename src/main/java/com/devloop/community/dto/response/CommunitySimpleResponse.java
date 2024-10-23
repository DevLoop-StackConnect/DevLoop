package com.devloop.community.dto.response;

import com.devloop.common.enums.Category;
import com.devloop.community.entity.Community;
import com.devloop.community.entity.ResolveStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommunitySimpleResponse {
    private final Long communityId;
    private final String title;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
    private final ResolveStatus status;
    private final Category category;

    private CommunitySimpleResponse(Long communityId, String title, LocalDateTime createdAt, LocalDateTime modifiedAt, ResolveStatus status, Category category) {
        this.communityId = communityId;
        this.title = title;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.status = status;
        this.category = category;
    }

    public static CommunitySimpleResponse from(Community community) {
        return new CommunitySimpleResponse(
                community.getId(),
                community.getTitle(),
                community.getCreatedAt(),
                community.getModifiedAt(),
                community.getResolveStatus(),
                community.getCategory()
        );
    }
}
