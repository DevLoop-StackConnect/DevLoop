package com.devloop.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
//slack 프리픽스를 가진 설정 속성을 매핑
@ConfigurationProperties(prefix = "slack")
public class SlackProperties {
    //slack 애플리케이션 관련 설정을 담는 필드
    private App app;
    //slack 알림 관련 설정을 담는 필드
    private Notification notification;

    @Getter
    @Setter
    public static class App {
        private String clientId;
        private String clientSecret;
        private String signingSecret;
        private String botToken;
        private String baseUrl;
    }

    @Getter
    @Setter
    public static class Notification {
        private String errorChannel;
    }
}