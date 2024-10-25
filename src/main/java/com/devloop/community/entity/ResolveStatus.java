package com.devloop.community.entity;

import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

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


