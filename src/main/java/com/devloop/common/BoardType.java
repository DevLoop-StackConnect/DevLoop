package com.devloop.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoardType {
    PARTY("스터디 모집 게시판"),
    COMMUNNITY("개발 커뮤니티 게시판"),
    PWT("프로젝트 with 튜터 게시판");

    private final String boardType;
}
