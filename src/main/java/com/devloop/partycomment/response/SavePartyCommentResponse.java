package com.devloop.partycomment.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SavePartyCommentResponse {
    private Long partyId;
    private Long commentId;
    private String comment;

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
