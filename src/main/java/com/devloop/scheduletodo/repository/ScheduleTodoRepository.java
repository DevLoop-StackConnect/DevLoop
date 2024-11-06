package com.devloop.scheduletodo.repository;

import com.devloop.scheduleboard.entity.ScheduleBoard;
import com.devloop.scheduletodo.entity.ScheduleTodo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleTodoRepository extends JpaRepository<ScheduleTodo, Long> {
    List<ScheduleTodo> findByScheduleBoard(ScheduleBoard scheduleBoard);
}
