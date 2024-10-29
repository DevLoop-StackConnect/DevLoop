package com.devloop.scheduleTodo.dto.request;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "시작일을 작성해 주세요")
    private LocalDateTime startDate;
    @NotBlank(message = "종료일을 작성해 주세요")
    private LocalDateTime endDate;
}
