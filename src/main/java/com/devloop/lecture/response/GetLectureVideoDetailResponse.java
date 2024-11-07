package com.devloop.lecture.response;

import lombok.Getter;

import java.net.URL;

@Getter
public class GetLectureVideoDetailResponse {
    private final String title;
    private final String videoURL;

    private GetLectureVideoDetailResponse(String title, String videoURL) {
        this.title = title;
        this.videoURL = videoURL;
    }
    public static GetLectureVideoDetailResponse of(String title, String videoURL) {
        return new GetLectureVideoDetailResponse(
                title,
                videoURL
        );
    }
}
