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

    private CommunitySimpleResponse(Long communityId, String title, String status, String category) {
        this.communityId = communityId;
        this.title = title;
        this.status = status;
        this.category = category;
    }

    public static CommunitySimpleResponse of(Long communityId, String title, String status, String category) {
        return new CommunitySimpleResponse(
                communityId,
                title,
                status,
                category
        );
    }
}
