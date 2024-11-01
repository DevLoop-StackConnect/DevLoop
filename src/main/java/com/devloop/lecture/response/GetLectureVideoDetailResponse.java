package com.devloop.lecture.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.URL;

@Getter
@NoArgsConstructor
public class GetLectureVideoDetailResponse {
    private String title;
    private URL videoURL;

    // 강의 시간
    private GetLectureVideoDetailResponse(String title,URL videoURL){
        this.title=title;
        this.videoURL=videoURL;
    }
    public static GetLectureVideoDetailResponse of(String title,URL videoURL){
        return new GetLectureVideoDetailResponse(
               title,
                videoURL
        );
    }
}
