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

    @NotNull
    private Long loginId;

    @Enumerated(EnumType.STRING)
    @NotNull
    private LoginType loginType = LoginType.SOCIAL;

    @NotNull
    private Long attachmentId;

    @NotNull
    private String username;

    @NotNull
    private String email;

    @NotNull
    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull
    private UserStatus status = UserStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    @NotNull
    private UserRole userRole;

    @NotNull
    private Long kakaoid;

    private User(String username, String email, String password, UserRole userRole) {
        this.loginId = 1L;
        this.attachmentId = 1L;
        this.username = username;
        this.email = email;
        this.password = password;
        this.userRole = userRole;
        this.kakaoid = 1L;
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
}
