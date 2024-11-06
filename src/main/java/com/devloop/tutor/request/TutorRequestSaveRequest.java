package com.devloop.tutor.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class TutorRequestSaveRequest {

    @NotBlank(message = "이름을 작성해 주세요.")
    private String name;

    @NotBlank(message = "깃허브 또는 블로그 주소를 입력해 주세요.")
    private String subUrl;

    @NotBlank(message = "계좌번호를 입력해 주세요.")
    private String accountNum;
}
