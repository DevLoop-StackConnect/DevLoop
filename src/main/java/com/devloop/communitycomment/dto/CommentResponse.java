package com.devloop.communitycomment.dto;

import com.devloop.communitycomment.entity.CommunityComment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponse {
    private final Long commentId;
    private final String content;
    private final String username;
    private final LocalDateTime createdAt;

    private CommentResponse(Long commentId, String content, String username,LocalDateTime createdAt) {
        this.commentId = commentId;
        this.content = content;
        this.username = username;
        this.createdAt=createdAt;
    }

    public static CommentResponse  of(Long commentId, String content, String username, LocalDateTime createdAt) {
        return new CommentResponse(commentId,
                content,
                username,
                createdAt);
    }
}
