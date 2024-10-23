package com.devloop.partycomment.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePartyCommentRequest {
    @NotBlank(message = "내용을 작성해 주세요")
    private String comment;
}