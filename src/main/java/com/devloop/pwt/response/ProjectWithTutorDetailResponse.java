package com.devloop.pwt.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProjectWithTutorDetailResponse {

    private final String title;
    private final String description;
    private final Integer price;
    private final String status;
    private final LocalDateTime deadline;
    private final Integer maxParticipants;
    private final String level;
    private final String tutorName;

    private ProjectWithTutorDetailResponse(
            String title,
            String description,
            Integer price,
            String status,
            LocalDateTime deadline,
            Integer maxParticipants,
            String level,
            String tutorName
    ) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.status = status;
        this.deadline = deadline;
        this.maxParticipants = maxParticipants;
        this.level = level;
        this.tutorName = tutorName;
    }

    public static ProjectWithTutorDetailResponse of(
            String title,
            String description,
            Integer price,
            String status,
            LocalDateTime deadline,
            Integer maxParticipants,
            String leve,
            String tutorName

    ){
        return new ProjectWithTutorDetailResponse(
                title,
                description,
                price,
                status,
                deadline,
                maxParticipants,
                leve,
                tutorName
        );
    }


}
