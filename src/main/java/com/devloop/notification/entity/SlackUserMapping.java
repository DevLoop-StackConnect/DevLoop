package com.devloop.notification.entity;

import com.devloop.common.Timestamped;
import com.devloop.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//Slack 연동 기능 (Slack 사용자 매핑, 알림 전송)
public class SlackUserMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(unique = true, nullable = false)
    private String slackId;
    @Column(nullable = false)
    private String slackEmail;

    private boolean active;

    private LocalDateTime lastVerifiedAt; // 마지막 검증 시간

    private LocalDateTime mappedAt;       // 매핑 생성 시간

    private SlackUserMapping(User user, String slackId, String slackEmail) {
        this.user = user;
        this.slackId = slackId;
        this.slackEmail = slackEmail;
        this.active = true;
        this.mappedAt = LocalDateTime.now(); // 매핑 생성 시간 설정
    }

    public static SlackUserMapping of(User user, String slackId, String slackEmail){
        return new SlackUserMapping(user, slackId, slackEmail);
    }

    //SlackId 와 이메일 업데이트
    public void updateSlackInfo(String slackId, String slackEmail) {
        this.slackId = slackId;
        this.slackEmail = slackEmail;
        this.active = true;
        this.mappedAt = LocalDateTime.now(); // 업데이트 시점에 매핑 생성 시간 갱신
    }
    //Slack 계정 비활성화
    public void deactivate() {
        this.active = false;
    }
    //Slack 계정 검증
    public void verify() {
        this.lastVerifiedAt = LocalDateTime.now(); // 검증 시간 갱신
    }
}