package com.devloop.tutor.response;

import com.devloop.tutor.entity.TutorRequest;
import com.devloop.user.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TutorRequestListAdminResponse {

    private final String name;
    private final String subUrl;
    private final LocalDateTime applicationDate;
    private final User user;

    private TutorRequestListAdminResponse(
            String name,
            String subUrl,
            LocalDateTime applicationDate,
            User user) {
        this.name = name;
        this.subUrl = subUrl;
        this.applicationDate = applicationDate;
        this.user = user;
    }

    public static TutorRequestListAdminResponse from(TutorRequest tutorRequest) {
        return new TutorRequestListAdminResponse(
                tutorRequest.getName(),
                tutorRequest.getSubUrl(),
                tutorRequest.getCreatedAt(),
                tutorRequest.getUser()
        );
    }
}
