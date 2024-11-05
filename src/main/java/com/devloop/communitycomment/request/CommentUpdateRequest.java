package com.devloop.communitycomment.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpdateRequest {
    @NotBlank(message = "내용을 작성해 주세요")
    private String content;
}
