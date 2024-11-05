package com.devloop.auth.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoUserInfo {
    private Long id;
    private String nickname;
    private String email;

    private KakaoUserInfo(Long id, String nickname, String email) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
    }

    public static KakaoUserInfo from(Long id, String nickname, String email) {
        return new KakaoUserInfo(id, nickname, email);
    }
}