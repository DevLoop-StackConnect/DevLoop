package com.devloop.user.enums;

import java.util.Arrays;

public enum UserRole {

    USER, TUTOR, ADMIN;

    public static UserRole of(String role) {
        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 권한입니다"));
    }

    public static class Authority {
        public static final String USER = "USER";
        public static final String TUTOR = "TUTOR";
        public static final String ADMIN = "ADMIN";
    }
}

