package com.devloop.lecture.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class LectureDetailResponse {
    private String title;
    private String description;
    private String recommend;
    private String category;
    private String level;
    private Integer price;
    private LocalDateTime created_at;
    private LocalDateTime modified_at;

    private LectureDetailResponse(
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

    public static LectureDetailResponse of(
            String title,
            String description,
            String recommend,
            String category,
            String level,
            Integer price,
            LocalDateTime created_at,
            LocalDateTime modified_at
    ){
        return new LectureDetailResponse(
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
