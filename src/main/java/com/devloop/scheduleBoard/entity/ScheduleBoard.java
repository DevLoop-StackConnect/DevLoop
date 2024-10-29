package com.devloop.scheduleBoard.entity;

import com.devloop.common.Timestamped;
import com.devloop.pwt.entity.ProjectWithTutor;
import com.devloop.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @ManyToOne
    @JoinColumn(name = "manager_tutor_id", nullable = false)
    private User managerTutor;//게시글 작성자(튜터)

    private ScheduleBoard(ProjectWithTutor projectWithTutor, User managerTutor) {
        this.projectWithTutor = projectWithTutor;
        this.managerTutor = managerTutor;
    }

    public static ScheduleBoard of(ProjectWithTutor projectWithTutor, User managerTutor) {
        return new ScheduleBoard(projectWithTutor, managerTutor);
    }
}
