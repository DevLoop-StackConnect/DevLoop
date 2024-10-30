package com.devloop.scheduleBoard.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Entity
@RequiredArgsConstructor
public class BoardAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_board_id", nullable = false)
    private ScheduleBoard scheduleBoard;

    //PWT purchase가 아직 없으니 purcahseId 대신 우선은 직접 User가져오기.
    @Column(name = "user_id", nullable = false) // 직접 User ID를 저장
    private Long userId;
//    @Column(name="purchase_id",nullable = false)//임시로 구매pk
//    private Long purchaseId; //추후에 구매 테이블이 구현되면 연관관계로 수정

    private BoardAssignment(ScheduleBoard scheduleBoard, Long userId){
        this.scheduleBoard=scheduleBoard;
        this.userId=userId;
    }

    public static BoardAssignment of(
            ScheduleBoard scheduleBoard,
            Long userId) {
        return new BoardAssignment(scheduleBoard,userId);
    }


}
