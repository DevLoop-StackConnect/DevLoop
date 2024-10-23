package com.devloop.communitycomment.dto;

import com.devloop.common.Timestamped;
import com.devloop.communitycomment.entity.CommunityComment;
import lombok.Getter;

@Getter
public class CommentResponse extends Timestamped {
    private final Long commentId;
    private final String content;
    private final String username;

    private CommentResponse(Long commentId, String content, String username) {
        this.commentId = commentId;
        this.content = content;
        this.username = username;
    }

    public static CommentResponse from(CommunityComment communityComment) {
        return new CommentResponse(
                communityComment.getId(),
                communityComment.getContent(),
                communityComment.getUser().getUsername()
        );
    }
}
