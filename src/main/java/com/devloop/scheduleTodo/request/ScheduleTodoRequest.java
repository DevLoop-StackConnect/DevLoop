package com.devloop.scheduleTodo.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ScheduleTodoRequest {
    @NotBlank(message = "제목을 작성해 주세요")
    private String title;
    @NotBlank(message = "내용을 작성해 주세요")
    private String content;
    @NotNull(message = "시작일을 작성해 주세요")
    private LocalDateTime startDate;
    @NotNull(message = "종료일을 작성해 주세요")
    private LocalDateTime endDate;
}
