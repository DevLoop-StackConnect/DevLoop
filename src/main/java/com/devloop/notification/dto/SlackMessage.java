package com.devloop.notification.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SlackMessage {
    //Slack 알림 메시지
    private String text;
    //Slack 알림 메시지 대상 채널
    private String channel;
}