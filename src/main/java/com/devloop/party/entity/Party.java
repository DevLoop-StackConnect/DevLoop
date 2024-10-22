package com.devloop.party.entity;


import com.devloop.common.Timestamped;
import com.devloop.party.enums.PartyStatus;
import com.devloop.party.request.SavePartyRequest;
import com.devloop.party.request.UpdatePartyRequest;
import com.devloop.party.response.UpdatePartyResponse;
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
    @Column(length = 255)
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

    public static Party from(SavePartyRequest savePartyRequest,User user){
        PartyStatus partyStatus=PartyStatus.valueOf(savePartyRequest.getStatus().toUpperCase());

        return new Party(
                savePartyRequest.getTitle(),
                savePartyRequest.getContents(),
                partyStatus,
                user
        );
    }

    public void update(UpdatePartyRequest updatePartyRequest){
        PartyStatus partyStatus=PartyStatus.valueOf(updatePartyRequest.getStatus().toUpperCase());
        this.title=updatePartyRequest.getTitle();
        this.contents=updatePartyRequest.getContents();
        this.status=partyStatus;
    }

}
