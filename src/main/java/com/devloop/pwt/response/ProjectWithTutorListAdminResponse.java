package com.devloop.pwt.response;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class ProjectWithTutorListAdminResponse {

    private final Long id;
    private final String title;
    private final BigDecimal price;
    private final String status;
    private final LocalDateTime deadline;
    private final Integer maxParticipants;
    private final String level;
    private final String tutorName;

    private ProjectWithTutorListAdminResponse(
            Long id,
            String title,
            BigDecimal price,
            String status,
            LocalDateTime deadline,
            Integer maxParticipants,
            String level,
            String tutorName
    ) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.status = status;
        this.deadline = deadline;
        this.maxParticipants = maxParticipants;
        this.level = level;
        this.tutorName = tutorName;
    }

    public static ProjectWithTutorListAdminResponse of(
            Long id,
            String title,
            BigDecimal price,
            String status,
            LocalDateTime deadline,
            Integer maxParticipants,
            String level,
            String tutorName
    ) {
        return new ProjectWithTutorListAdminResponse(
                id,
                title,
                price,
                status,
                deadline,
                maxParticipants,
                level,
                tutorName
        );
    }
}
