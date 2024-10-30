package com.devloop.pwt.entity;

import com.devloop.common.Timestamped;
import com.devloop.common.enums.Approval;
import com.devloop.common.enums.BoardType;
import com.devloop.common.enums.Category;
import com.devloop.product.entity.Product;
import com.devloop.pwt.enums.Level;
import com.devloop.pwt.enums.ProjectWithTutorStatus;
import com.devloop.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@RequiredArgsConstructor
@Table
public class ProjectWithTutor extends Product {


    @Enumerated(EnumType.STRING)
    private BoardType boardType = BoardType.PWT;


    @NotNull
    @Column(columnDefinition = "TEXT")
    private String description;


    @NotNull
    @Enumerated(EnumType.STRING)
    private ProjectWithTutorStatus status = ProjectWithTutorStatus.IN_PROGRESS;

    @NotNull
    private LocalDateTime deadline;

    @NotNull
    private Integer maxParticipants;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Level level;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Approval approval = Approval.WAITE;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private ProjectWithTutor(
            String title,
            String description,
            BigDecimal price,
            LocalDateTime deadline,
            Integer maxParticipants,
            Level level,
            Category category,
            User user
    ) {
        super(title, price);
        this.description = description;
        this.deadline = deadline;
        this.maxParticipants = maxParticipants;
        this.level = level;
        this.category = category;
        this.user = user;
    }

    public static ProjectWithTutor of(
            String title,
            String description,
            BigDecimal price,
            LocalDateTime deadline,
            Integer maxParticipants,
            Level level,
            Category category,
            User user
    ) {
        return new ProjectWithTutor(
                title,
                description,
                price,
                deadline,
                maxParticipants,
                level,
                category,
                user
        );
    }

    public void update(
            String title,
            String description,
            BigDecimal price,
            LocalDateTime deadline,
            Integer maxParticipants,
            Level level,
            User user,
            Category category
    ) {
        super.update(title, price);
        this.description = description;
        this.deadline = deadline;
        this.maxParticipants = maxParticipants;
        this.level = level;
        this.user = user;
        this.category = category;
    }

    public void changeApproval(Approval approval) {
        this.approval = approval;
    }

}
