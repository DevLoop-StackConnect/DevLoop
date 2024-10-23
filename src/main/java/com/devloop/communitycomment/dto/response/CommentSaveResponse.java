package com.devloop.communitycomment.dto.response;

import com.devloop.common.Timestamped;
import com.devloop.communitycomment.entity.CommunityComment;
import lombok.Getter;

@Getter
public class CommentSaveResponse extends Timestamped {
    private final Long commentId;
    private final String content;

    private CommentSaveResponse(Long commentId, String content) {
        this.commentId = commentId;
        this.content = content;
    }

    public static CommentSaveResponse from(CommunityComment communityComment) {
        return new CommentSaveResponse(
                communityComment.getId(),
                communityComment.getContent()
        );
    }
}
