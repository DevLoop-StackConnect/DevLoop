package com.devloop.community.dto.response;

import com.devloop.common.Timestamped;
import com.devloop.common.enums.Category;
import com.devloop.community.entity.Community;
import com.devloop.community.entity.ResolveStatus;
import com.devloop.communitycomment.dto.CommentResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class CommunityDetailResponse extends Timestamped {
    private final Long communityId;
    private final String title;
    private final String content;
    private final ResolveStatus status;
    private final Category category;
    private final List<CommentResponse> comments;

    private CommunityDetailResponse(Long communityId, String title, String content, ResolveStatus status, Category category, List<CommentResponse> comments) {
        this.communityId = communityId;
        this.title = title;
        this.content = content;
        this.status = status;
        this.category = category;
        this.comments = comments;
    }

    //코드컨벤션에 맞춰서 정적 팩토리 메서드 추가
    public static CommunityDetailResponse from(Community community, List<CommentResponse> comments) {
        return new CommunityDetailResponse(
                community.getId(),
                community.getTitle(),
                community.getContent(),
                community.getResolveStatus(),
                community.getCategory(),
                comments
        );
    }
}
