package com.devloop.scheduleboard.entity;

import com.devloop.purchase.entity.Purchase;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_board_id", nullable = false)
    private ScheduleBoard scheduleBoard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="purchase_id",nullable = false)
    private Purchase purchase;

    private BoardAssignment(ScheduleBoard scheduleBoard, Purchase purchase){
        this.scheduleBoard=scheduleBoard;
        this.purchase=purchase;
    }

    public static BoardAssignment of(
            ScheduleBoard scheduleBoard,
            Purchase purchase) {
        return new BoardAssignment(scheduleBoard,purchase);
    }
}
