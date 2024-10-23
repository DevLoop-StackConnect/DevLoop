package com.devloop.pwt.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProjectWithTutorSaveRequest {

    @NotBlank(message = "제목을 입력해 주세요.")
    private String title;
    @NotBlank(message = "내용을 입력해 주세요.")
    private String description;
    @NotBlank(message = "가격을 입력해 주세요.")
    private Integer price;
    @NotBlank(message = "마감일을 입력해 주세요.")
    private LocalDateTime deadline;
    @NotBlank(message = "참여할 수 있는 최대 인원을 입력해 주세요.")
    private Integer maxParticipants;
    @NotBlank(message = "난이도를 선택해 주세요.")
    private String level;
}
