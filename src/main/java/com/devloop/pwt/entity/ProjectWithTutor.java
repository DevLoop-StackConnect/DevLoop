package com.devloop.pwt.entity;

import com.devloop.common.Timestamped;
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
            User user
    ) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.status = status;
        this.deadline = deadline;
        this.maxParticipants = maxParticipants;
        this.user = user;
    }

    public static ProjectWithTutor from(
            String title,
            String description,
            Integer price,
            ProjectWithTutorStatus status,
            LocalDateTime deadline,
            Integer maxParticipants,
            User user
    ){
        return new ProjectWithTutor(
                title,
                description,
                price,
                status,
                deadline,
                maxParticipants,
                user
        );
    }


}
