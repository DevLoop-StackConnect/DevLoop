package com.devloop.scheduleboard.entity;

import com.devloop.common.Timestamped;
import com.devloop.pwt.entity.ProjectWithTutor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class ScheduleBoard extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_board_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "project_with_tutor_id", nullable = false)
    private ProjectWithTutor projectWithTutor;

    @OneToMany(mappedBy = "scheduleBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardAssignment> boardAssignments = new ArrayList<>();

    private ScheduleBoard(ProjectWithTutor projectWithTutor) {
        this.projectWithTutor = projectWithTutor;
    }

    public static ScheduleBoard of(ProjectWithTutor projectWithTutor) {
        return new ScheduleBoard(projectWithTutor);
    }


}
