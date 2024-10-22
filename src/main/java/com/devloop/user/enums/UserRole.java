package com.devloop.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum UserRole {


    ROLE_USER(Authority.USER),
    ROLE_TUTOR(Authority.TUTOR),
    ROLE_ADMIN(Authority.ADMIN);


    private final String role;

    public static UserRole of(String role) {
        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 권한입니다"));
    }

    public static class Authority {
        public static final String USER = "ROLE_USER";
        public static final String TUTOR = "ROLE_TUTOR";
        public static final String ADMIN = "ROLE_ADMIN";
    }
}

