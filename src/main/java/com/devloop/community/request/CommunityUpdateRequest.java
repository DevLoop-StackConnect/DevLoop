package com.devloop.community.request;

import lombok.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.devloop.common.enums.Category;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import com.devloop.community.entity.ResolveStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CommunityUpdateRequest {
    @NotBlank(message = "제목을 작성해 주세요")
    private String title;
    @NotBlank(message = "내용을 작성해 주세요")
    private String content;
    @NotNull(message = "해결 상태를 작성해 주세요")
    private ResolveStatus status;
    @NotNull(message = "카테고리를 작성해 주세요")
    private Category category;
}

