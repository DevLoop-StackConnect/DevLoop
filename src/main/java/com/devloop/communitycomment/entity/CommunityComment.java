package com.devloop.communitycomment.entity;

import lombok.Getter;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import com.devloop.user.entity.User;
import com.devloop.common.Timestamped;
import com.devloop.community.entity.Community;

@Entity
@Getter
@NoArgsConstructor
public class CommunityComment extends Timestamped {

    @Id
    @Column(name = "communityComment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "communityComment_content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //커뮤 댓글 생성자
    private CommunityComment(String content, Community community, User user) {
        this.content = content;
        this.community = community;
        this.user = user;
    }

    public static CommunityComment of(String content, Community community, User user) {
        return new CommunityComment(
                content,
                community,
                user
        );
    }

    //커뮤 댓글 수정 메서드
    public void updateContent(String newContent) {
        this.content = newContent;
    }
}
