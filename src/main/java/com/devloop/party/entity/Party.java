package com.devloop.party.entity;

import com.devloop.common.Timestamped;
import com.devloop.common.enums.BoardType;
import com.devloop.common.enums.Category;
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

    @Enumerated(EnumType.STRING)
    private BoardType boardType = BoardType.PARTY;

    @NotNull
    @Column(length = 20)
    private String title;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String contents;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PartyStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;



    private Party(String title,String contents, PartyStatus status, Category category,User user){
        this.title=title;
        this.contents=contents;
        this.status=status;
        this.category=category;
        this.user=user;
    }

    public static Party from(SavePartyRequest request,User user){
        return new Party(
                request.getTitle(),
                request.getContents(),
                PartyStatus.of(request.getStatus()),
                Category.of(request.getCategory()),
                user
        );
    }

    public void update(UpdatePartyRequest request){
        this.title=request.getTitle();
        this.contents=request.getContents();
        this.status=PartyStatus.of(request.getStatus());
        this.category=Category.of(request.getCategory());
    }

}
