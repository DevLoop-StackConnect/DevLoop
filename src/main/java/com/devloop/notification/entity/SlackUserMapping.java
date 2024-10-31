package com.devloop.notification.entity;

import com.devloop.common.Timestamped;
import com.devloop.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_slack_mappings")
public class SlackUserMapping extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(unique = true)
    private String slackId;

    private String slackEmail;
    private boolean active;

    private LocalDateTime lastVerifiedAt; // 마지막 검증 시간
    private LocalDateTime mappedAt;       // 매핑 생성 시간

    @Builder
    public SlackUserMapping(User user, String slackId, String slackEmail) {
        this.user = user;
        this.slackId = slackId;
        this.slackEmail = slackEmail;
        this.active = true;
        this.mappedAt = LocalDateTime.now(); // 매핑 생성 시간 설정
    }

    public void updateSlackInfo(String slackId, String slackEmail) {
        this.slackId = slackId;
        this.slackEmail = slackEmail;
        this.active = true;
        this.mappedAt = LocalDateTime.now(); // 업데이트 시점에 매핑 생성 시간 갱신
    }

    public void deactivate() {
        this.active = false;
    }

    public void verify() {
        this.lastVerifiedAt = LocalDateTime.now(); // 검증 시간 갱신
    }
}