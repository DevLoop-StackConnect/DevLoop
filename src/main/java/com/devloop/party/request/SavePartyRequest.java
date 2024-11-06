package com.devloop.party.request;

import com.devloop.common.enums.Category;
import com.devloop.party.enums.PartyStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SavePartyRequest {
    @NotBlank(message = "제목을 작성해 주세요")
    private String title;
    @NotBlank(message = "내용을 작성해 주세요")
    private String contents;
    @NotNull(message = "상태를 입력해주세요.")
    private PartyStatus status;
    @NotNull(message = "카테고리를 입력해주세요.")
    private Category category;
}
