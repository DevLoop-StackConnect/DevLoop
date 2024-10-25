package com.devloop.community.dto.response;

import com.devloop.common.enums.Category;
import com.devloop.community.entity.ResolveStatus;
import lombok.Getter;

@Getter
public class CommunitySimpleResponse {
    private final Long communityId;
    private final String title;
    private final String status;
    private final String category;

    private CommunitySimpleResponse(Long communityId, String title, ResolveStatus resolveStatus, Category category) {
        this.communityId = communityId;
        this.title = title;
        this.status = resolveStatus.getDescription();
        this.category = category.getDescription();
    }

    public static CommunitySimpleResponse of(Long communityId, String title, ResolveStatus resolveStatus, Category category) {
        return new CommunitySimpleResponse(
                communityId,
                title,
                resolveStatus,
                category
        );
    }
}
