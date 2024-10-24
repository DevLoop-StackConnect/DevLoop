package com.devloop.pwt.response;

import com.devloop.common.apipayload.dto.ProjectWithTutorResponseDto;
import com.devloop.pwt.enums.Level;
import com.devloop.pwt.enums.ProjectWithTutorStatus;
import com.devloop.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class ProjectWithTutorListResponse {

    private final Long id;
    private final String title;
    private final Integer price;
    private final String status;
    private final LocalDate deadline;
    private final Integer maxParticipants;
    private final String level;
    private final String tutorName;

    private ProjectWithTutorListResponse(
            Long id,
            String title,
            Integer price,
            ProjectWithTutorStatus status,
            LocalDate deadline,
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

    public static ProjectWithTutorListResponse from(
            ProjectWithTutorResponseDto project
    ){
        return new ProjectWithTutorListResponse(
                project.getId(),
                project.getTitle(),
                project.getPrice(),
                project.getStatus(),
                LocalDate.from(project.getDeadline()),
                project.getMaxParticipants(),
                project.getLevel(),
                project.getUser()
        );
    }

}
