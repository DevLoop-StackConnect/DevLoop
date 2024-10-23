package com.devloop.communitycomment.dto;

import com.devloop.communitycomment.entity.CommunityComment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponse {
    private final Long commentId;
    private final String content;
    private final String username; //가져와지나
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    private CommentResponse(Long commentId, String content, String username, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.commentId = commentId;
        this.content = content;
        this.username = username;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static CommentResponse from(CommunityComment communityComment) {
        return new CommentResponse(
                communityComment.getId(),
                communityComment.getContent(),
                communityComment.getUser().getUsername(),
                communityComment.getCreatedAt(),
                communityComment.getModifiedAt()
        );
    }
}
