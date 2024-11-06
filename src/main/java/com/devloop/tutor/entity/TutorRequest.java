package com.devloop.tutor.entity;

import com.devloop.common.Timestamped;
import com.devloop.common.enums.Approval;
import com.devloop.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TutorRequest extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tutorRequestId;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 255)
    private String subUrl;

    @Column(nullable = false, length = 20)
    private String accountNum;

    @Enumerated(EnumType.STRING)
    private Approval status = Approval.WAITE;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)  // 단방향 참조
    private User user;

    private TutorRequest(
            String name,
            String subUrl,
            String accountNum,
            User userId
    ) {
        this.name = name;
        this.subUrl = subUrl;
        this.accountNum = accountNum;
        this.user = userId;
    }

    public static TutorRequest of(
            String name,
            String subUrl,
            String accountNum,
            User userId
    ) {
        return new TutorRequest(
                name,
                subUrl,
                accountNum,
                userId
        );
    }

    public void changeStatus(Approval newStatus) {
        this.status = newStatus;
    }

}
