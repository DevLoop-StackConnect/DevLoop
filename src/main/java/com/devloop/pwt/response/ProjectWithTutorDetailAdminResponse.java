package com.devloop.pwt.response;

import com.devloop.pwt.entity.ProjectWithTutor;
import com.devloop.pwt.enums.Level;
import com.devloop.pwt.enums.ProjectWithTutorStatus;
import com.devloop.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ProjectWithTutorDetailAdminResponse {

    private final String title;
    private final String description;
    private final Integer price;
    private final String status;
    private final LocalDateTime deadline;
    private final Integer maxParticipants;
    private final String level;
    private final User user;


    private ProjectWithTutorDetailAdminResponse(
            String title,
            String description,
            Integer price,
            ProjectWithTutorStatus status,
            LocalDateTime deadline,
            Integer maxParticipants,
            Level level,
            User user) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.status = status.getStatus();
        this.deadline = deadline;
        this.maxParticipants = maxParticipants;
        this.level = level.getLevel();
        this.user = user;
    }

    public static ProjectWithTutorDetailAdminResponse from(
            ProjectWithTutor project
    ){
        return new ProjectWithTutorDetailAdminResponse(
                project.getTitle(),
                project.getDescription(),
                project.getPrice(),
                project.getStatus(),
                project.getDeadline(),
                project.getMaxParticipants(),
                project.getLevel(),
                project.getUser()
        );
    }

}
