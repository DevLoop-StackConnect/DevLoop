package com.devloop.lecture.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class GetLectureDetailResponse {
    private String title;
    private String tutorName;
    private String description;
    private String recommend;
    private String category;
    private String level;
    private BigDecimal price;
    private Integer videoCount;
    private Integer reviewCount;
    private LocalDateTime created_at;
    private LocalDateTime modified_at;

    private GetLectureDetailResponse(
            String title,
            String tutorName,
            String description,
            String recommend,
            String category,
            String level,
            BigDecimal price,
            Integer videoCount,
            Integer reviewCount,
            LocalDateTime created_at,
            LocalDateTime modified_at
    ){
        this.title=title;
        this.tutorName=tutorName;
        this.description=description;
        this.recommend=recommend;
        this.category=category;
        this.level=level;
        this.price=price;
        this.videoCount=videoCount;
        this.reviewCount=reviewCount;
        this.created_at=created_at;
        this.modified_at=modified_at;
    }

    public static GetLectureDetailResponse of(
            String title,
            String tutorName,
            String description,
            String recommend,
            String category,
            String level,
            BigDecimal price,
            Integer videoCount,
            Integer reviewCount,
            LocalDateTime created_at,
            LocalDateTime modified_at
    ){
        return new GetLectureDetailResponse(
                title,
                tutorName,
                description,
                recommend,
                category,
                level,
                price,
                videoCount,
                reviewCount,
                created_at,
                modified_at
        );
    }
}
