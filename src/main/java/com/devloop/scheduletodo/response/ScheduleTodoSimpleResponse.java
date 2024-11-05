package com.devloop.scheduletodo.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ScheduleTodoSimpleResponse {
    private final String title;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    private ScheduleTodoSimpleResponse(String title, LocalDateTime startDate, LocalDateTime endDate) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    public static ScheduleTodoSimpleResponse of(String title, LocalDateTime startDate, LocalDateTime endDate){
        return new ScheduleTodoSimpleResponse(
                title,
                startDate,
                endDate
        );
    }
}
