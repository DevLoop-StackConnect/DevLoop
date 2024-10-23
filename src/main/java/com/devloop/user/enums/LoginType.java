package com.devloop.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoginType {
    SOCIAL("소셜 로그인 유저입니다."),
    LOCAL("일반 로그인 유저입니다.");

    private final String loginType;
}
