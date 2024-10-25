package com.devloop.pwt.enums;

import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.user.enums.UserRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Level {
    EASY("쉬움"),
    MEDIUM("중간"),
    HARD("어려움");

    private final String level;

    public static Level of(String level) {
        return Arrays.stream(Level.values())
                .filter(l -> l.name().equalsIgnoreCase(level))
                .findFirst()
                .orElseThrow(() -> new ApiException(ErrorStatus._LEVEL_NOT_EXIST));
    }
}
