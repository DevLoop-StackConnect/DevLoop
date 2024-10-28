package com.devloop.tutor.response;

import com.devloop.common.apipayload.dto.UserResponseDto;
import com.devloop.tutor.entity.TutorRequest;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TutorRequestListAdminResponse {

    private final String name;
    private final String subUrl;
    private final LocalDateTime applicationDate;
    private final UserResponseDto user;

    private TutorRequestListAdminResponse(
            String name,
            String subUrl,
            LocalDateTime applicationDate,
            UserResponseDto user
    ) {
        this.name = name;
        this.subUrl = subUrl;
        this.applicationDate = applicationDate;
        this.user = user;
    }

    public static TutorRequestListAdminResponse of(
            String name,
            String subUrl,
            LocalDateTime applicationDate,
            UserResponseDto user
    ) {
        return new TutorRequestListAdminResponse(
                name,
                subUrl,
                applicationDate,
                user
        );
    }
}
