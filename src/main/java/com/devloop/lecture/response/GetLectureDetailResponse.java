package com.devloop.lecture.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class GetLectureDetailResponse {
    private String title;
    private String description;
    private String recommend;
    private String category;
    private String level;
    private Integer price;
    private LocalDateTime created_at;
    private LocalDateTime modified_at;

    //강의 총 개수
    //강의 총 시간
    //강의 후기 개수
    private GetLectureDetailResponse(
            String title,
            String description,
            String recommend,
            String category,
            String level,
            Integer price,
            LocalDateTime created_at,
            LocalDateTime modified_at
    ){
        this.title=title;
        this.description=description;
        this.recommend=recommend;
        this.category=category;
        this.level=level;
        this.price=price;
        this.created_at=created_at;
        this.modified_at=modified_at;
    }

    public static GetLectureDetailResponse of(
            String title,
            String description,
            String recommend,
            String category,
            String level,
            Integer price,
            LocalDateTime created_at,
            LocalDateTime modified_at
    ){
        return new GetLectureDetailResponse(
                title,
                description,
                recommend,
                category,
                level,
                price,
                created_at,
                modified_at
        );
    }
}
