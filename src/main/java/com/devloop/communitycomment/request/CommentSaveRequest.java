package com.devloop.communitycomment.request;

import lombok.*;
import jakarta.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentSaveRequest {

    @NotBlank(message = "내용을 입력해 주세요")
    private String content;
}
