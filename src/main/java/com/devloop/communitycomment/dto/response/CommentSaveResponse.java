package com.devloop.communitycomment.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentSaveResponse {
    private final Long commentId;
    private final String content;
    private final LocalDateTime createdAt;

    public CommentSaveResponse(Long commentId, String content, LocalDateTime createdAt) {
        this.commentId = commentId;
        this.content = content;
        this.createdAt = createdAt;
    }
}
