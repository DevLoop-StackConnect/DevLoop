package com.devloop.notification.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SlackLinkRequest {
    private String slackId;
    private String slackEmail;
}