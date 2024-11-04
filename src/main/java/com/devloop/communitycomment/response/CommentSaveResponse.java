package com.devloop.communitycomment.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentSaveResponse {
    private final Long commentId;
    private final String content;
    private final LocalDateTime createdAt;

    private CommentSaveResponse(Long commentId, String content, LocalDateTime createdAt) {
        this.commentId = commentId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public static CommentSaveResponse of(Long commentId, String content, LocalDateTime createdAt) {
        return new CommentSaveResponse(commentId,
                content,
                createdAt);
    }
}
