package com.devloop.pwt.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProjectWithTutorStatus {
    IN_PROGRESS("모집중"),
    COMPLETED("모집종료");

    private final String status;
}
