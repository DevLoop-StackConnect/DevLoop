package com.devloop.lecture.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VideoStatus {
    FAILED("업로드 실패"),
    PENDING("업로드 전"),
    IN_PROGRESS("업로드 중"),
    COMPLETED("업로드 성공");

    private final String status;
}
