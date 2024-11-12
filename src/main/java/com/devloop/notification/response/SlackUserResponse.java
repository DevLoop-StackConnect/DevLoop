package com.devloop.notification.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class SlackUserResponse {

    private boolean ok;
    private User user;
    private String error;

    @Getter
    @ToString
    public static class User{
        private String id;
        private String name;
        private String teamId;
        private Profile profile;
        private boolean isBot;
    }

    @Getter
    @ToString
    public static class Profile{
        private String email;
        private String realName;
        private String displayName;
    }
}
