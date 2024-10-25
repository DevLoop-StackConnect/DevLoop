package com.devloop.communitycomment.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentUpdateResponse {
    private final Long commentId;
    private final String content;
    private final LocalDateTime modifiedAt;

    private CommentUpdateResponse(Long commentId, String content, LocalDateTime modifiedAt) {
        this.commentId = commentId;
        this.content = content;
        this.modifiedAt = modifiedAt;
    }

    public static CommentUpdateResponse of(Long commentId, String content, LocalDateTime modifiedAt) {
        return new CommentUpdateResponse(commentId,
                content,
                modifiedAt);
    }
}
