package com.devloop.lecture.response;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class GetLectureDetailResponse {
    private final String title;
    private final String tutorName;
    private final String description;
    private final String recommend;
    private final String category;
    private final String level;
    private final BigDecimal price;
    private final Integer videoCount;
    private final Integer reviewCount;
    private final LocalDateTime created_at;
    private final LocalDateTime modified_at;

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
    ) {
        this.title = title;
        this.tutorName = tutorName;
        this.description = description;
        this.recommend = recommend;
        this.category = category;
        this.level = level;
        this.price = price;
        this.videoCount = videoCount;
        this.reviewCount = reviewCount;
        this.created_at = created_at;
        this.modified_at = modified_at;
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
    ) {
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
