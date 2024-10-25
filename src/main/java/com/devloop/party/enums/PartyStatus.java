package com.devloop.party.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum PartyStatus {
    IN_PROGRESS("모집중"),
    COMPLETED("모집완료");

    private final String status;

    public static PartyStatus of(String status) {
        return Arrays.stream(PartyStatus.values())
                .filter(s -> s.name().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상태 입니다."));
    }
}
