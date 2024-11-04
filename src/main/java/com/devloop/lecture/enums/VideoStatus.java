package com.devloop.lecture.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VideoStatus {
    PENDING("업로드 전"),
    COMPLETED("업로드 완료");

    private final String status;
}
