package com.devloop.partycomment.response;

import com.devloop.partycomment.entity.PartyComment;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetPartyCommentListResponse {
    private Long commentId;
    private String comment;

    private GetPartyCommentListResponse(Long commentId,String comment){
        this.commentId=commentId;
        this.comment=comment;
    }

    public static GetPartyCommentListResponse from(PartyComment partyComment){
        return new GetPartyCommentListResponse(
                partyComment.getId(),
                partyComment.getComment()
        );
    }
}
