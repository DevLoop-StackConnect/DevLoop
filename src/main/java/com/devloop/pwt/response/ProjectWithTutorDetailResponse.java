package com.devloop.pwt.response;

import lombok.Getter;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;

@Getter
public class ProjectWithTutorDetailResponse {

    private final String title;
    private final String description;
    private final BigDecimal price;
    private final String status;
    private final LocalDateTime deadline;
    private final Integer maxParticipants;
    private final String level;
    private final String tutorName;
    private final URL attachmentUrl;

    private ProjectWithTutorDetailResponse(
            String title,
            String description,
            BigDecimal price,
            String status,
            LocalDateTime deadline,
            Integer maxParticipants,
            String level,
            String tutorName,
            URL attachmentUrl
    ) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.status = status;
        this.deadline = deadline;
        this.maxParticipants = maxParticipants;
        this.level = level;
        this.tutorName = tutorName;
        this.attachmentUrl = attachmentUrl;
    }

    public static ProjectWithTutorDetailResponse of(
            String title,
            String description,
            BigDecimal price,
            String status,
            LocalDateTime deadline,
            Integer maxParticipants,
            String level,
            String tutorName,
            URL attachmentUrl
    ) {
        return new ProjectWithTutorDetailResponse(
                title,
                description,
                price,
                status,
                deadline,
                maxParticipants,
                level,
                tutorName,
                attachmentUrl
        );
    }


}
