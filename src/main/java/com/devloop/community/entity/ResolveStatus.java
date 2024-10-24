package com.devloop.community.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ResolveStatus {
    SOLVED("해결"),
    UNSOLVED("미해결");

    private final String description;

    public String getDescription(){
        return this.description;
    }

    public static ResolveStatus fromString(String status) {
        return Arrays.stream(ResolveStatus.values())
                .filter(s -> s.name().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 해결 상태 입력값입니다. 오타를 확인하세요 ->  " + status));
    }
}


