package com.devloop.scheduleBoard.repository;

import com.devloop.scheduleBoard.entity.BoardAssignment;
import com.devloop.scheduleBoard.entity.ScheduleBoard;
import com.devloop.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardAssignmentRepository extends JpaRepository<BoardAssignment,Long> {
    boolean existsByScheduleBoardAndPurchase_User(ScheduleBoard scheduleBoard, User user);
}
