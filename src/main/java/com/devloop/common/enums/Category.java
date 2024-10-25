package com.devloop.common.enums;

import com.devloop.party.enums.PartyStatus;
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

    public static Category of(String category) {
        return Arrays.stream(Category.values())
                .filter(c -> c.name().equalsIgnoreCase(category))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 입니다."));
    }

}
