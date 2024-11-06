package com.devloop.pwt.response;

import com.devloop.common.apipayload.dto.UserResponseDto;
import lombok.Getter;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;

@Getter
public class ProjectWithTutorDetailAdminResponse {

    private final String title;
    private final String description;
    private final BigDecimal price;
    private final String status;
    private final LocalDateTime deadline;
    private final Integer maxParticipants;
    private final String level;
    private final URL attachmentUrl;
    private final UserResponseDto user;

    private ProjectWithTutorDetailAdminResponse(
            String title,
            String description,
            BigDecimal price,
            String status,
            LocalDateTime deadline,
            Integer maxParticipants,
            String level,
            URL attachmentUrl,
            UserResponseDto user
    ) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.status = status;
        this.deadline = deadline;
        this.maxParticipants = maxParticipants;
        this.level = level;
        this.attachmentUrl = attachmentUrl;
        this.user = user;
    }

    public static ProjectWithTutorDetailAdminResponse of(
            String title,
            String description,
            BigDecimal price,
            String status,
            LocalDateTime deadline,
            Integer maxParticipants,
            String level,
            URL attachmentUrl,
            UserResponseDto user
    ) {
        return new ProjectWithTutorDetailAdminResponse(
                title,
                description,
                price,
                status,
                deadline,
                maxParticipants,
                level,
                attachmentUrl,
                user
        );
    }

}
