package com.devloop.partycomment.response;

import lombok.Getter;

@Getter
public class GetPartyCommentListResponse {
    private final String userName;
    private final Long commentId;
    private final String comment;

    private GetPartyCommentListResponse(String userName, Long commentId, String comment) {
        this.userName = userName;
        this.commentId = commentId;
        this.comment = comment;
    }

    public static GetPartyCommentListResponse of(String userName, Long commentId, String comment) {
        return new GetPartyCommentListResponse(
                userName,
                commentId,
                comment
        );
    }
}
