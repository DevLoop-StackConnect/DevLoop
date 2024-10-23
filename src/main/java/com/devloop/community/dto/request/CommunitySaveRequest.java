package com.devloop.community.dto.request;

import com.devloop.common.enums.Category;
import com.devloop.community.entity.ResolveStatus;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "해결 상태를 작성해 주세요")
    private ResolveStatus status; //게시글 해결 상태 (SOLVED, UNSOLVED)
    @NotBlank(message = "카테고리를 작성해 주세요")
    private Category category;
}
