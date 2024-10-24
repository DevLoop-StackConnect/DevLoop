package com.devloop.community.dto.response;

import com.devloop.common.enums.Category;
import com.devloop.community.entity.Community;
import com.devloop.community.entity.ResolveStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommunitySaveResponse  {
    private final Long communityId;
    private final String title;
    private final String content;
    private final String status; //게시글 해결 상태 (SOLVED, UNSOLVED)
    private final String category;
    private final LocalDateTime createdAt;


    private CommunitySaveResponse(Long communityId, String title, String content, String status, String category, LocalDateTime createdAt) {
        this.communityId = communityId;
        this.title = title;
        this.content = content;
        this.status = status;
        this.category = category;
        this.createdAt=createdAt;
    }

    public static CommunitySaveResponse from(Community community) {
        return new CommunitySaveResponse(
                community.getId(),
                community.getTitle(),
                community.getContent(),
                community.getResolveStatus().getDescription(),
                community.getCategory().getDescription(),
                community.getCreatedAt()
        );
    }

}
