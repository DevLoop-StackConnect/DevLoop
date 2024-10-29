package com.devloop.scheduleTodo.repository;

import com.devloop.scheduleTodo.entity.ScheduleTodo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleTodoRepository extends JpaRepository<ScheduleTodo,Long> {
}
