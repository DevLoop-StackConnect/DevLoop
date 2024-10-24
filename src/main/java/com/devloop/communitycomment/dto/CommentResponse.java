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

    public static CommentResponse from(CommunityComment communityComment) {
        return new CommentResponse(
                communityComment.getId(),
                communityComment.getContent(),
                communityComment.getUser().getUsername(),
                communityComment.getCreatedAt()
        );
    }
}
