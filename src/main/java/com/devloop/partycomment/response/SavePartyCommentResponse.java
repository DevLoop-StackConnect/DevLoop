package com.devloop.partycomment.response;

import lombok.Getter;

@Getter
public class SavePartyCommentResponse {
    private final Long partyId;
    private final Long commentId;
    private final String comment;

    private SavePartyCommentResponse(Long partyId,Long commentId,String comment){
        this.partyId=partyId;
        this.commentId=commentId;
        this.comment=comment;
    }

    public static SavePartyCommentResponse of(Long partyId,Long commentId,String comment){
        return new SavePartyCommentResponse(
                partyId,
                commentId,
                comment
        );
    }
}
