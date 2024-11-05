package com.devloop.lecture.response;

import lombok.Getter;

import java.net.URL;

@Getter
public class GetLectureVideoDetailResponse {
    private final String title;
    private final URL videoURL;

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
