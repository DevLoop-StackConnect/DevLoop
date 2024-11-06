package com.devloop.notification.request;

import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SlackLinkRequest {
    @Column(nullable = false)
    private String slackId;
    @Column(nullable = false)
    private String slackEmail;
}