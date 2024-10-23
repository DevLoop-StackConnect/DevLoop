package com.devloop.partycomment.response;

import com.devloop.partycomment.entity.PartyComment;
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

    public static UpdatePartyCommentResponse from(Long partyId, PartyComment partyComment){
        return new UpdatePartyCommentResponse(
                partyId,
                partyComment.getId(),
                partyComment.getComment()
        );
    }
}
