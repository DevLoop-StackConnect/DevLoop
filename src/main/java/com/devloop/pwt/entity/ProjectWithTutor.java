package com.devloop.pwt.entity;

import com.devloop.common.enums.Approval;
import com.devloop.common.enums.BoardType;
import com.devloop.common.enums.Category;
import com.devloop.product.entity.Product;
import com.devloop.pwt.enums.Level;
import com.devloop.pwt.enums.ProjectWithTutorStatus;
import com.devloop.scheduleboard.entity.ScheduleBoard;
import com.devloop.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectWithTutor extends Product {

    @Enumerated(EnumType.STRING)
    private BoardType boardType = BoardType.PWT;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    private ProjectWithTutorStatus status = ProjectWithTutorStatus.IN_PROGRESS;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Column(nullable = false)
    private Integer maxParticipants;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Level level;

    @Enumerated(EnumType.STRING)
    private Approval approval = Approval.WAITE;

    @Enumerated(EnumType.STRING)
    private Category category = Category.ETC;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(mappedBy = "projectWithTutor", cascade = CascadeType.ALL)
    private ScheduleBoard scheduleBoard;

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

    public void changeStatus(ProjectWithTutorStatus projectWithTutorStatus) {
        this.status = projectWithTutorStatus;
    }

    public void saveScheduleBoard(ScheduleBoard scheduleBoard) {
        this.scheduleBoard = scheduleBoard;
    }
}
