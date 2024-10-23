package com.devloop.user.dto.response;

import com.devloop.user.enums.UserRole;
import lombok.Getter;

@Getter
public class UserResponse {
    private final String userName;
    private final String userEmail;
    private final UserRole userRole;

    private UserResponse(final String userName, final String userEmail, final UserRole userRole) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userRole = userRole;
    }

    public static UserResponse from(final String userName, final String userEmail, final UserRole userRole) {
        return new UserResponse(userName, userEmail, userRole);
    }
}
