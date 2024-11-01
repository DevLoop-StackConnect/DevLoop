package com.devloop.scheduleTodo.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ScheduleTodoResponse {
    private final Long id;
    private final String createdBy;
    private final String title;
    private final String content;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    private ScheduleTodoResponse(Long id, String createdBy,String title, String content, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.createdBy=createdBy;
        this.title = title;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static ScheduleTodoResponse of(Long id,  String createdBy, String title, String content, LocalDateTime startDate, LocalDateTime endDate){
        return new ScheduleTodoResponse(
                id,
                createdBy,
                title,
                content,
                startDate,
                endDate
        );
    }
}
