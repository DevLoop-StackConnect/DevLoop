package com.devloop.scheduletodo.entity;

import com.devloop.common.Timestamped;
import com.devloop.scheduleboard.entity.ScheduleBoard;
import com.devloop.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleTodo extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_todo")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_board_id", nullable = false)
    private ScheduleBoard scheduleBoard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    // Version 필드 추가_낙관적락
    @Version
    private Long version;


    private String title;
    private String content;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private ScheduleTodo(ScheduleBoard scheduleBoard,
                         User createdBy,
                         String title,
                         String content,
                         LocalDateTime startDate,
                         LocalDateTime endDate) {
        this.scheduleBoard = scheduleBoard;
        this.createdBy = createdBy;
        this.title = title;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static ScheduleTodo of(ScheduleBoard scheduleBoard,
                                  User createdBy,
                                  String title,
                                  String content,
                                  LocalDateTime startDate,
                                  LocalDateTime endDate) {
        return new ScheduleTodo(scheduleBoard,
                createdBy,
                title,
                content,
                startDate,
                endDate);
    }

    public void updateScheduleTodo(String title, String content, LocalDateTime startDate, LocalDateTime endDate) {
        this.title = title;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}