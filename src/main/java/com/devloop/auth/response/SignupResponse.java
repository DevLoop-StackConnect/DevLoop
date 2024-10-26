package com.devloop.auth.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SignupResponse {

    private final Long id;
    private final String email;
    private final String name;
    private final LocalDateTime createdAt;

    private SignupResponse(Long id, String email, String name, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.createdAt = createdAt;
    }

    public static SignupResponse of(Long id, String email, String name, LocalDateTime createdAt) {
        return new SignupResponse(id ,email, name, createdAt);
    }
}

