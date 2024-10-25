package com.devloop.community.dto.request;

import com.devloop.common.enums.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class CommunitySaveRequest {
    @NotBlank(message = "제목을 작성해 주세요")
    private String title;
    @NotBlank(message = "내용을 작성해 주세요")
    private String content;
    @NotNull(message = "카테고리를 작성해 주세요")
    private String category;
}
