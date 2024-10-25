package com.devloop.pwt.entity;

import com.devloop.common.Timestamped;
import com.devloop.common.enums.Approval;
import com.devloop.pwt.enums.Level;
import com.devloop.pwt.enums.ProjectWithTutorStatus;
import com.devloop.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@RequiredArgsConstructor
@Table
public class ProjectWithTutor extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(length = 255)
    private String title;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    private Integer price;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ProjectWithTutorStatus status;

    @NotNull
    private LocalDateTime deadline;

    @NotNull
    private Integer maxParticipants;

    @NotNull
    private Level level;

    @NotNull
    private Approval approval;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    private ProjectWithTutor(
            String title,
            String description,
            Integer price,
            ProjectWithTutorStatus status,
            LocalDateTime deadline,
            Integer maxParticipants,
            Level level,
            Approval approval,
            User user
    ) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.status = status;
        this.deadline = deadline;
        this.maxParticipants = maxParticipants;
        this.level = level;
        this.approval = approval;
        this.user = user;
    }

    public static ProjectWithTutor of(
            String title,
            String description,
            Integer price,
            ProjectWithTutorStatus status,
            LocalDateTime deadline,
            Integer maxParticipants,
            Level level,
            Approval approval,
            User user
    ){
        return new ProjectWithTutor(
                title,
                description,
                price,
                status,
                deadline,
                maxParticipants,
                level,
                approval,
                user
        );
    }

    public void update(
            String title,
            String description,
            Integer price,
            LocalDateTime deadline,
            Integer maxParticipants,
            Level level,
            User user
    ) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.deadline = deadline;
        this.maxParticipants = maxParticipants;
        this.level = level;
        this.user = user;
    }

    public void changeApproval(Approval approval) {
        this.approval = approval;
    }
}
