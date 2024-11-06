package com.devloop.lecture.response;

import lombok.Getter;

@Getter
public class SaveLectureResponse {
    private final Long lectureId;

    private SaveLectureResponse(Long lectureId) {
        this.lectureId = lectureId;
    }
    public static SaveLectureResponse of(Long lectureId) {
        return new SaveLectureResponse(lectureId);
    }
}
