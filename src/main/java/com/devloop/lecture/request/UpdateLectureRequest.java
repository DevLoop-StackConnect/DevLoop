package com.devloop.lecture.request;

import com.devloop.common.enums.Category;
import com.devloop.pwt.enums.Level;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateLectureRequest {
    @NotBlank(message = "제목을 작성해 주세요")
    private String title;
    @NotBlank(message = "설명을 작성해 주세요")
    private String description;
    @NotBlank(message = "추천인을 작성해 주세요")
    private String recommend;
    @NotNull(message = "카테고리를 작성해 주세요")
    private Category category;
    @NotNull(message = "난이도를 작성해 주세요")
    private Level level;
    @NotNull(message = "가격을 작성해주세요")
    private BigDecimal price;
}
