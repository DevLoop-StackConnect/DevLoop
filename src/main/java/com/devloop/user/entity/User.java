package com.devloop.user.entity;

import com.devloop.common.Timestamped;
import com.devloop.user.enums.UserRole;
import com.devloop.user.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table
public class User extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;

    private Long kakaoid;

    public User(String username, String email, String password, UserRole userRole) {
        this.username = username;
        this.email = email;
        this.userRole = userRole;
        this.password = password;
    }

    public void update() {
        this.status = UserStatus.WITHDRAWAL;
    }
}
