package com.devloop.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "slack")
public class SlackProperties {
    private App app;
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