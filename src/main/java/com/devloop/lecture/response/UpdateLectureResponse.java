package com.devloop.lecture.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateLectureResponse {
    private Long lectureId;

    private UpdateLectureResponse (Long lectureId){
        this.lectureId=lectureId;
    }
    public static UpdateLectureResponse of(Long lectureId){
        return new UpdateLectureResponse(lectureId);
    }
}
