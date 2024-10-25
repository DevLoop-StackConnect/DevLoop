package com.devloop.pwt.response;

import com.devloop.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
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
            String status,
            LocalDateTime deadline,
            Integer maxParticipants,
            String level,
            User user
    ) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.status = status;
        this.deadline = deadline;
        this.maxParticipants = maxParticipants;
        this.level = level;
        this.user = user;
    }

    public static ProjectWithTutorDetailAdminResponse of(
            String title,
            String description,
            Integer price,
            String status,
            LocalDateTime deadline,
            Integer maxParticipants,
            String level,
            User user
    ){
        return new ProjectWithTutorDetailAdminResponse(
                title,
                description,
                price,
                status,
                deadline,
                maxParticipants,
                level,
                user
        );
    }

}