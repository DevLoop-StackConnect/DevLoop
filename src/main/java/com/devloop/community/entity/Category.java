package com.devloop.community.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    APP_DEV("앱개발"),
    WEB_DEV("웹개발"),
    GAME_DEV("게임개발");

    private final String description;

}
