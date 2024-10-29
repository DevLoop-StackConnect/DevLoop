package com.devloop.lecture.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LectureListResponse {
    private String title;
    private String category;
    private String level;
    private Integer price;

    private LectureListResponse(String title,String category,String level,Integer price){
        this.title=title;
        this.category=category;
        this.level=level;
        this.price=price;
    }
    public static LectureListResponse of(String title,String category,String level,Integer price){
        return new LectureListResponse(
                title,
                category,
                level,
                price
        );
    }
}
