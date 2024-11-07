package com.devloop.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoardType {
    PARTY("스터디 모집 게시판"),
    COMMUNITY("개발 커뮤니티 게시판"),
    PWT("프로젝트 with 튜터 게시판"),
    LECTURE("강의 목록");

    private final String boardType;
}
