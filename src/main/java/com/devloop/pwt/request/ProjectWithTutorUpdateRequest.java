package com.devloop.pwt.request;

import com.devloop.common.enums.Category;
import com.devloop.pwt.enums.Level;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectWithTutorUpdateRequest {

    @NotBlank(message = "제목을 입력해 주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해 주세요.")
    private String description;

    @NotNull(message = "가격을 입력해 주세요.")
    private BigDecimal price;

    @NotNull(message = "마감일을 입력해 주세요.")
    @Future(message = "마감일은 현재보다 미래 시간이어야 합니다.")
    private LocalDateTime deadline;

    @NotNull(message = "참여할 수 있는 최대 인원을 입력해 주세요.")
    private Integer maxParticipants;

    @NotNull(message = "난이도를 선택해 주세요.")
    private Level level;

    @NotNull(message = "카테고리를 입력해 주세요.")
    private Category category;
}
