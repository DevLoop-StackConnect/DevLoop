package com.devloop.community.entity;

import lombok.Getter;

import java.util.Arrays;

import lombok.RequiredArgsConstructor;
import com.devloop.common.exception.ApiException;
import com.devloop.common.apipayload.status.ErrorStatus;

@Getter
@RequiredArgsConstructor
public enum ResolveStatus {
    SOLVED("해결"),
    UNSOLVED("미해결");

    private final String description;

    public static ResolveStatus of(String status) {
        return Arrays.stream(ResolveStatus.values())
                .filter(s -> s.name().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new ApiException(ErrorStatus._STATUS_NOT_EXSIST));
    }
}


