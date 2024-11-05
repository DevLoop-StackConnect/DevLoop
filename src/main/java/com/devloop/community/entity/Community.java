package com.devloop.community.entity;

import lombok.*;

import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.*;
import com.devloop.user.entity.User;
import com.devloop.common.Timestamped;
import com.devloop.common.enums.Category;
import com.devloop.common.enums.BoardType;
import com.devloop.communitycomment.entity.CommunityComment;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Community extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "community_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private BoardType boardType = BoardType.COMMUNITY;

    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private Category category = Category.ETC;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status")
    private ResolveStatus resolveStatus = ResolveStatus.UNSOLVED; //기본값 필드로 설정

    @OneToMany(mappedBy = "community", fetch = FetchType.LAZY)
    private List<CommunityComment> communityComments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    //커뮤니티 글 생성자
    private Community(String title, String content, Category category, User user) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.user = user;
    }

    public static Community of(String title, String content, Category category, User user) {
        return new Community(
                title,
                content,
                category,
                user
        );
    }

    //커뮤니티 글 수정 메서드
    public void updateCommunity(String title, String content, ResolveStatus resolveStatus, Category category) {
        this.title = title;
        this.content = content;
        this.resolveStatus = resolveStatus;
        this.category = category;
    }
}
