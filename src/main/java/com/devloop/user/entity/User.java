
package com.devloop.user.entity;

import com.devloop.common.Timestamped;
import com.devloop.notification.entity.SlackUserMapping;
import com.devloop.user.enums.LoginType;
import com.devloop.user.enums.UserRole;
import com.devloop.user.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private LoginType loginType = LoginType.LOCAL;

    @Column
    private Long attachmentId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private UserRole userRole = UserRole.ROLE_USER;

    private Long kakaoId;
    private String loginId;

    private String slackId;
    private String slackEmail;
    private boolean slackLinked;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private SlackUserMapping slackMapping;

    private User(String username, String email, String password, UserRole userRole) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.userRole = userRole;
    }

    public static User of(String username, String email, String password, UserRole userRole) {
        return new User(username, email, password, userRole);
    }

    public void update() {
        this.status = UserStatus.WITHDRAWAL;
    }

    public void updateProfileImg(Long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public void changeUserRoleToTutor(UserRole userRole) {
        this.userRole = userRole;
    }

    public User(String username, String password, String email, UserRole userRole, Long kakaoId) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.userRole = userRole;
        this.kakaoId = kakaoId;
        this.loginType = LoginType.SOCIAL;
        this.loginId = email;
        this.attachmentId = 1L;
    }

    public User kakaoIdUpdate(Long kakaoId) {
        this.kakaoId = kakaoId;
        return this;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void updateSlackInfo(String slackId, String slackEmail) {
        this.slackId = slackId;
        this.slackEmail = slackEmail;
        this.slackLinked = true;
    }

    public void unlinkSlack() {
        this.slackId = null;
        this.slackEmail = null;
        this.slackLinked = false;

    }
}

