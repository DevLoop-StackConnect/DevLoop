package com.devloop.partycomment.entity;

import com.devloop.common.Timestamped;
import com.devloop.party.entity.Party;
import com.devloop.partycomment.request.SavePartyCommentRequest;
import com.devloop.partycomment.request.UpdatePartyCommentRequest;
import com.devloop.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class PartyComment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String comment;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name="party_id")
    private Party party;

    private PartyComment(String comment,User user,Party party){
        this.comment=comment;
        this.user=user;
        this.party=party;
    }

    public static PartyComment from(SavePartyCommentRequest request,User user,Party party){
        return new PartyComment(
                request.getComment(),
                user,
                party
        );
    }

    public void update(UpdatePartyCommentRequest request){
        this.comment= request.getComment();
    }
}
