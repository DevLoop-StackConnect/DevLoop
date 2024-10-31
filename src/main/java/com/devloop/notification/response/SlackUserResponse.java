package com.devloop.notification.response;

import lombok.Data;

@Data
public class SlackUserResponse {

    private boolean ok;
    private User user;
    private String error;

    @Data
    public static class User{
        private String id;
        private String name;
        private String teamId;
        private Profile profile;
        private boolean isBot;
    }

    @Data
    public static class Profile{
        private String email;
        private String realName;
        private String displayName;
    }
}
