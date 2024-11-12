package com.devloop.lecture.response;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class GetLectureListResponse {
    private final Long id;
    private final String title;
    private final String category;
    private final String level;
    private final String boardType;
    private final BigDecimal price;

    private GetLectureListResponse(Long id, String title, String category, String boardType, String level, BigDecimal price) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.boardType = boardType;
        this.level = level;
        this.price = price;
    }
    public static GetLectureListResponse of(Long id, String title, String category, String boardType, String level, BigDecimal price) {
        return new GetLectureListResponse(
                id,
                title,
                category,
                boardType,
                level,
                price
        );
    }
}
