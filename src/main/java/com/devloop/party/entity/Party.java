package com.devloop.party.entity;

import com.devloop.common.Timestamped;
import com.devloop.common.enums.BoardType;
import com.devloop.common.enums.Category;
import com.devloop.party.enums.PartyStatus;
import com.devloop.party.request.SavePartyRequest;
import com.devloop.party.request.UpdatePartyRequest;
import com.devloop.partycomment.entity.PartyComment;
import com.devloop.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Party extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private BoardType boardType = BoardType.PARTY;

    @Column(length = 20, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contents;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private PartyStatus status = PartyStatus.IN_PROGRESS;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Category category = Category.ETC;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "party", cascade = CascadeType.REMOVE)
    private List<PartyComment> comments;

    private Party(String title, String contents, PartyStatus status, Category category, User user) {
        this.title = title;
        this.contents = contents;
        this.status = status;
        this.category = category;
        this.user = user;
    }

    public static Party from(SavePartyRequest request, User user) {
        return new Party(
                request.getTitle(),
                request.getContents(),
                request.getStatus(),
                request.getCategory(),
                user
        );
    }

    public void update(UpdatePartyRequest request) {
        this.title = request.getTitle();
        this.contents = request.getContents();
        this.status = request.getStatus();
        this.category = request.getCategory();
    }


}
