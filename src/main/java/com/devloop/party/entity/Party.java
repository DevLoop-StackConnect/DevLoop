package com.devloop.party.entity;

import com.devloop.common.Timestamped;
import com.devloop.party.enums.PartyStatus;
import com.devloop.party.request.SavePartyRequest;
import com.devloop.party.request.UpdatePartyRequest;
import com.devloop.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Party extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(length = 20)
    private String title;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String contents;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PartyStatus status;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    private Party(String title,String contents, PartyStatus status,User user){
        this.title=title;
        this.contents=contents;
        this.status=status;
        this.user=user;
    }

    public static Party from(SavePartyRequest request,User user){
        PartyStatus partyStatus=PartyStatus.valueOf(request.getStatus().toUpperCase());

        return new Party(
                request.getTitle(),
                request.getContents(),
                partyStatus,
                user
        );
    }

    public void update(UpdatePartyRequest request){
        PartyStatus partyStatus=PartyStatus.valueOf(request.getStatus().toUpperCase());
        this.title=request.getTitle();
        this.contents=request.getContents();
        this.status=partyStatus;
    }

}
