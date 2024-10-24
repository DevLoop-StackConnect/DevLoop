package com.devloop.auth.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SignupResponse {

    private final String email;
    private final String name;
    private final LocalDateTime createdAt;

    private SignupResponse(String email, String name, LocalDateTime createdAt) {
        this.email = email;
        this.name = name;
        this.createdAt = createdAt;
    }

    public static SignupResponse from(String email, String name, LocalDateTime createdAt) {
        return new SignupResponse(email, name, createdAt);
    }
}

