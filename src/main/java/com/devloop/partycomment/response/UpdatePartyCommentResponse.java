package com.devloop.partycomment.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdatePartyCommentResponse {
    private Long partyId;
    private Long commentId;
    private String comment;

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
