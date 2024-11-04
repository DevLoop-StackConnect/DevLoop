package com.devloop.lecture.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class SaveLectureRequest {
    private String title;
    private String description;
    private String recommend;
    private String category;
    private String level;
    private BigDecimal price;
}
