package com.devloop.pwt.response;

import com.devloop.common.apipayload.dto.ProjectWithTutorResponseDto;
import com.devloop.pwt.enums.Level;
import com.devloop.pwt.enums.ProjectWithTutorStatus;
import com.devloop.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ProjectWithTutorListAdminResponse {

    private final Long id;
    private final String title;
    private final Integer price;
    private final String status;
    private final LocalDateTime deadline;
    private final Integer maxParticipants;
    private final String level;
    private final String tutorName;

    private ProjectWithTutorListAdminResponse(
            Long id,
            String title,
            Integer price,
            ProjectWithTutorStatus status,
            LocalDateTime deadline,
            Integer maxParticipants,
            Level level,
            User tutorName
    ) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.status = status.getStatus();
        this.deadline = deadline;
        this.maxParticipants = maxParticipants;
        this.level = level.getLevel();
        this.tutorName = tutorName.getUsername();
    }

    public static ProjectWithTutorListAdminResponse from(
            ProjectWithTutorResponseDto project
    ) {
        return new ProjectWithTutorListAdminResponse(
                project.getId(),
                project.getTitle(),
                project.getPrice(),
                project.getStatus(),
                project.getDeadline(),
                project.getMaxParticipants(),
                project.getLevel(),
                project.getUser()
        );
    }
}
