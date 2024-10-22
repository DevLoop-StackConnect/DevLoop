package com.devloop.party.entity;


import com.devloop.common.Timestamped;
import com.devloop.party.enums.PartyStatus;
import com.devloop.party.request.SavePartyRequest;
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

    private Party(String title,String contents, PartyStatus status){
        this.title=title;
        this.contents=contents;
        this.status=status;
    }

    public static Party from(SavePartyRequest savePartyRequest){
        PartyStatus partyStatus=PartyStatus.valueOf(savePartyRequest.getStatus().toUpperCase());
        return new Party(
                savePartyRequest.getTitle(),
                savePartyRequest.getContents(),
                partyStatus
        );
    }

}
