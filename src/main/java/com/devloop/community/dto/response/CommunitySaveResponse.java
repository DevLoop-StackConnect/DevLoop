package com.devloop.community.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommunitySaveResponse {
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
        this.createdAt = createdAt;
    }

    public static CommunitySaveResponse of(Long communityId, String title, String content, String status, String category, LocalDateTime createdAt) {
        return new CommunitySaveResponse(
                communityId,
                title,
                content,
                status,
                category,
                createdAt
        );
    }

}
