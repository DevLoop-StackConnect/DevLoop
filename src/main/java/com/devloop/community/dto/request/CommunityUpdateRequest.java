package com.devloop.community.dto.request;

import com.devloop.common.enums.Category;
import com.devloop.community.entity.ResolveStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "해결 상태를 작성해 주세요")
    private String status;
    @NotNull(message = "카테고리를 작성해 주세요")
    private String category;

    public ResolveStatus getResolvedStatus() {
        return ResolveStatus.fromString(status);
    }

    public Category getCategory() {
        return Category.fromString(this.category);
    }

}

