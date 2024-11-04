package com.devloop.lecture.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetLectureVideoListResponse {
    private Long id;
    private String title;

    // 강의 시간
    private GetLectureVideoListResponse(Long id, String title){
        this.id=id;
        this.title=title;
    }
    public static GetLectureVideoListResponse of(Long id,String title){
        return new GetLectureVideoListResponse(
                id,
                title
        );
    }


}
