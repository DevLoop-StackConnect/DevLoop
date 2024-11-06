package com.devloop.scheduleboard.repository;

import com.devloop.scheduleboard.entity.BoardAssignment;
import com.devloop.scheduleboard.entity.ScheduleBoard;
import com.devloop.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardAssignmentRepository extends JpaRepository<BoardAssignment, Long> {
    boolean existsByScheduleBoardAndPurchase_User(ScheduleBoard scheduleBoard, User user);
}
