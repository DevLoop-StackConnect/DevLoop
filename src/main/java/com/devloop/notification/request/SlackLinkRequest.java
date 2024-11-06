package com.devloop.notification.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SlackLinkRequest {
    @NotBlank(message = "Slack 아이디를 입력해주세요.")
    private String slackId;
    @NotBlank(message = "Slack 이메일을 입력해주세요.")
    private String slackEmail;
}