package com.devloop.partycomment.response;

import lombok.Getter;

@Getter
public class UpdatePartyCommentResponse {
    private final Long partyId;
    private final Long commentId;
    private final String comment;

    private UpdatePartyCommentResponse(Long partyId,Long commentId,String comment){
        this.partyId=partyId;
        this.commentId=commentId;
        this.comment=comment;
    }

    public static UpdatePartyCommentResponse of(Long partyId,Long commentId,String comment){
        return new UpdatePartyCommentResponse(
                partyId,
                commentId,
                comment
        );
    }
}
