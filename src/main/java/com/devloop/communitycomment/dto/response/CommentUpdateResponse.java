package com.devloop.communitycomment.dto.response;

import com.devloop.communitycomment.entity.CommunityComment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentUpdateResponse {
    private final Long commentId;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    private CommentUpdateResponse(Long commentId, String content, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.commentId = commentId;
        this.content = content;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static CommentUpdateResponse from(CommunityComment communityComment) {
        return new CommentUpdateResponse(
                communityComment.getId(),
                communityComment.getContent(),
                communityComment.getCreatedAt(),
                communityComment.getModifiedAt()
        );
    }
}
