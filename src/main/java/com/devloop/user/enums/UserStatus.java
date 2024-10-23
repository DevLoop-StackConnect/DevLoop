package com.devloop.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {

    ACTIVE("계정 활성 상태"),
    WITHDRAWAL("계정 탈퇴 상태");

    private final String UserStatus;
}
