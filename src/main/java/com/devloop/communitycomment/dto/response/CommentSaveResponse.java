package com.devloop.communitycomment.dto.response;

import com.devloop.communitycomment.entity.CommunityComment;
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

    public static CommentSaveResponse from(CommunityComment communityComment) {
        return new CommentSaveResponse(
                communityComment.getId(),
                communityComment.getContent(),
                communityComment.getCreatedAt()
        );
    }
}
