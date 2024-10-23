package com.devloop.tutor.entity;

import com.devloop.common.Timestamped;
import com.devloop.common.enums.Approval;
import com.devloop.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@RequiredArgsConstructor
@Table
public class TutorRequest extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tutorRequestId;

    @NotNull
    @Column(length = 20)
    private String name;

    @NotNull
    @Column(length = 255)
    private String subUrl;

    @NotNull
    @Column(length = 20)
    private String accountNum;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Approval status;

    @NotNull
    @OneToOne
    @JoinColumn(name="user_id")  // 단방향 참조
    private User user;

    private TutorRequest(String name, String subUrl, String accountNum, Approval status, User userId) {
        this.name = name;
        this.subUrl = subUrl;
        this.accountNum = accountNum;
        this.status = status;
        this.user = userId;
    }

    public static TutorRequest from(String name, String subUrl, String accountNum, Approval status, User userId){
        return new TutorRequest(
                name,
                subUrl,
                accountNum,
                status,
                userId
        );
    }

    public void changeStatus(Approval newStatus) {
        this.status = newStatus;
    }




}
