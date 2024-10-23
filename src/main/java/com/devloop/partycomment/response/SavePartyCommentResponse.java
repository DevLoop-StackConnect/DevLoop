package com.devloop.partycomment.response;

import com.devloop.partycomment.entity.PartyComment;
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

    public static SavePartyCommentResponse from(Long partyId,PartyComment partyComment){
        return new SavePartyCommentResponse(
                partyId,
                partyComment.getId(),
                partyComment.getComment()
        );
    }
}
