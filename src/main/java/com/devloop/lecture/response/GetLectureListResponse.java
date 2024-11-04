package com.devloop.lecture.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class GetLectureListResponse {
    private Long id;
    private String title;
    private String category;
    private String level;
    private BigDecimal price;

    private GetLectureListResponse(Long id, String title, String category, String level, BigDecimal price){
        this.id=id;
        this.title=title;
        this.category=category;
        this.level=level;
        this.price=price;
    }
    public static GetLectureListResponse of(Long id, String title, String category, String level, BigDecimal price){
        return new GetLectureListResponse(
                id,
                title,
                category,
                level,
                price
        );
    }
}
