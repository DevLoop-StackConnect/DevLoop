package com.devloop.attachment.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Domain {
    PROFILE("유저 프로필"),
    PWT("project with tutor"),
    PARTY("스터디 파티"),
    COMMUNITY("개발 커뮤니티");

    private final String domain;
}
