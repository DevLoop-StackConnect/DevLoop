package com.devloop.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@ConfigurationProperties(prefix = "kakao")
@Configuration
public class KakaoProperties {

    private final Client client = new Client();
    private final Auth auth = new Auth();

    @Getter
    @Setter
    public static class Client {
        private String id;
        private String redirectUri;
    }

    @Getter
    @Setter
    public static class Auth {
        private String baseUrl;
    }
}