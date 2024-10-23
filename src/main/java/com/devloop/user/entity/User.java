package com.devloop.user.entity;

import com.devloop.common.Timestamped;
import com.devloop.user.enums.LoginType;
import com.devloop.user.enums.UserRole;
import com.devloop.user.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table
public class User extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private LoginType loginType = LoginType.LOCAL;

    private Long attachmentId;

    @NotNull
    private String username;

    @NotNull
    private String email;

    @NotNull
    private String password;

    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    @NotNull
    private UserRole userRole;

    @Column(nullable = true)
    private Long kakaoId;

    @Column(nullable = true)
    private String loginId;

    private User(String username, String email, String password, UserRole userRole) {
        this.attachmentId = 1L;
        this.username = username;
        this.email = email;
        this.password = password;
        this.userRole = userRole;
        this.loginType = LoginType.LOCAL;
    }

    public static User from(String username, String email, String password, UserRole userRole) {
        return new User(username, email, password, userRole);
    }

    public void update() {
        this.status = UserStatus.WITHDRAWAL;}

    public void updateProfileImg(Long attachmentId){
        this.attachmentId = attachmentId;}

    public void changeUserRoleToTutor(UserRole userRole) {
        this.userRole = userRole;
    }

    public User(String username, String password, String email, UserRole userRole, Long kakaoId) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.userRole = userRole;
        this.kakaoId =kakaoId;
        this.loginType = LoginType.SOCIAL;
        this.loginId = email;
        this.attachmentId = 1L;
    }

    public User kakaoIdUpdate(Long kakaoId) {
        this.kakaoId = kakaoId;
        return this;
    }
}
