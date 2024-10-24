package com.devloop.community.dto.response;

import com.devloop.common.Timestamped;
import com.devloop.common.enums.Category;
import com.devloop.community.entity.Community;
import com.devloop.community.entity.ResolveStatus;
import lombok.Getter;

@Getter
public class CommunitySimpleResponse {
    private final Long communityId;
    private final String title;
    private final String status;
    private final Category category;

    private CommunitySimpleResponse(Long communityId, String title, String status, Category category) {
        this.communityId = communityId;
        this.title = title;
        this.status = status;
        this.category = category;
    }

    public static CommunitySimpleResponse from(Community community) {
        return new CommunitySimpleResponse(
                community.getId(),
                community.getTitle(),
                community.getResolveStatus().getDescription(),
                community.getCategory()
        );
    }
}
