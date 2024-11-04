package com.devloop.lecture.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SaveLectureResponse {
    private Long lectureId;

    private SaveLectureResponse (Long lectureId){
        this.lectureId=lectureId;
    }
    public static SaveLectureResponse of(Long lectureId){
        return new SaveLectureResponse(lectureId);
    }
}
