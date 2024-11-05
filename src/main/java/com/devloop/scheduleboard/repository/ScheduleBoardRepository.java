package com.devloop.scheduleboard.repository;

import com.devloop.pwt.entity.ProjectWithTutor;
import com.devloop.scheduleboard.entity.ScheduleBoard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScheduleBoardRepository extends JpaRepository<ScheduleBoard,Long> {
    Optional<ScheduleBoard> findByProjectWithTutor(ProjectWithTutor projectWithTutor);
}
