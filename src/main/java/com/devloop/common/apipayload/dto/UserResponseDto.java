package com.devloop.common.apipayload.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class UserResponseDto {

    private final String username;
    private final String email;

    private UserResponseDto(
            String username,
            String email
    ) {
        this.username = username;
        this.email = email;
    }

    public static UserResponseDto of(
            String username,
            String email
    ) {
        return new UserResponseDto(
                username,
                email
        );
    }

}
