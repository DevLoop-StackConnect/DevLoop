package com.devloop.community.dto.request;

import com.devloop.common.enums.Category;
import com.devloop.community.entity.ResolveStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommunityUpdateRequest {
    @NotBlank(message = "제목을 작성해 주세요")
    private String title;
    @NotBlank(message = "내용을 작성해 주세요")
    private String content;
    @NotBlank(message = "해결 상태를 작성해 주세요")
    private ResolveStatus status;
    @NotBlank(message = "카테고리를 작성해 주세요")
    private Category category;
}
