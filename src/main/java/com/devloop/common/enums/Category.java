package com.devloop.common.enums;

import com.devloop.party.enums.PartyStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    APP_DEV("앱개발"),
    WEB_DEV("웹개발"),
    GAME_DEV("게임개발");

    private final String description;

    public static Category of(String category) {
        return Category.valueOf(category.toUpperCase());
    }

}
