package com.devloop.lecture.response;

import lombok.Getter;

@Getter
public class UpdateLectureResponse {
    private final Long lectureId;

    private UpdateLectureResponse(Long lectureId) {
        this.lectureId = lectureId;
    }
    public static UpdateLectureResponse of(Long lectureId) {
        return new UpdateLectureResponse(lectureId);
    }
}
