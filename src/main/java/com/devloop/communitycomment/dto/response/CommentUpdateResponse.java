package com.devloop.communitycomment.dto.response;

import com.devloop.common.Timestamped;
import com.devloop.communitycomment.entity.CommunityComment;
import lombok.Getter;

@Getter
public class CommentUpdateResponse extends Timestamped {
    private final Long commentId;
    private final String content;

    private CommentUpdateResponse(Long commentId, String content) {
        this.commentId = commentId;
        this.content = content;
    }

    public static CommentUpdateResponse from(CommunityComment communityComment) {
        return new CommentUpdateResponse(
                communityComment.getId(),
                communityComment.getContent()
        );
    }
}
