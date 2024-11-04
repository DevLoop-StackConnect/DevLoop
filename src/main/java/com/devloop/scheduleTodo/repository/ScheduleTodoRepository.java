package com.devloop.scheduleTodo.repository;

import com.devloop.scheduleBoard.entity.ScheduleBoard;
import com.devloop.scheduleTodo.entity.ScheduleTodo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleTodoRepository extends JpaRepository<ScheduleTodo,Long> {
    List<ScheduleTodo> findByScheduleBoard(ScheduleBoard scheduleBoard);
}
