package com.devloop.pwt.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ProjectWithTutorSaveRequest {

    @NotBlank(message = "제목을 입력해 주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해 주세요.")
    private String description;

    @NotNull(message = "가격을 입력해 주세요.")
    private Integer price;

    @NotNull(message = "마감일을 입력해 주세요.")
    @Future(message = "마감일은 현재보다 미래 시간이어야 합니다.")
    private LocalDateTime deadline;

    @NotNull(message = "참여할 수 있는 최대 인원을 입력해 주세요.")
    private Integer maxParticipants;

    @NotBlank(message = "난이도를 선택해 주세요.")
    private String level;
}
