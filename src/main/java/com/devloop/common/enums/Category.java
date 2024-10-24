package com.devloop.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Category {
    APP_DEV("앱개발"),
    WEB_DEV("웹개발"),
    GAME_DEV("게임개발");

    private final String description;

    public String getDescription() {
        return this.description;
    }

    public static Category fromString(String category) {
        return Arrays.stream(Category.values())
                .filter(c -> c.name().equalsIgnoreCase(category))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 카테고리 입력값입니다. 오타를 확인하세요 -> " + category));
    }

}
