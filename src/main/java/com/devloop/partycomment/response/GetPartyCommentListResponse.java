package com.devloop.partycomment.response;

import com.devloop.partycomment.entity.PartyComment;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetPartyCommentListResponse {
    private String userName;
    private Long commentId;
    private String comment;

    private GetPartyCommentListResponse(String userName,Long commentId,String comment){
        this.userName=userName;
        this.commentId=commentId;
        this.comment=comment;
    }

    public static GetPartyCommentListResponse of(String userName,Long commentId,String comment){
        return new GetPartyCommentListResponse(
                userName,
                commentId,
                comment
        );
    }
}
