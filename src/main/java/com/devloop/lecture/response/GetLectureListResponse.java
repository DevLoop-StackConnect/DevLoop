package com.devloop.lecture.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetLectureListResponse {
    private Long id;
    private String title;
    private String category;
    private String level;
    private Integer price;

    //튜터 이름
    //후기 평균
    private GetLectureListResponse(Long id, String title, String category, String level, Integer price){
        this.id=id;
        this.title=title;
        this.category=category;
        this.level=level;
        this.price=price;
    }
    public static GetLectureListResponse of(Long id, String title, String category, String level, Integer price){
        return new GetLectureListResponse(
                id,
                title,
                category,
                level,
                price
        );
    }
}
