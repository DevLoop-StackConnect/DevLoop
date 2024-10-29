package com.devloop.lecture.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateLectureRequest {
    private String title;
    private String description;
    private String recommend;
    private String category;
    private String level;
    private Integer price;
}
