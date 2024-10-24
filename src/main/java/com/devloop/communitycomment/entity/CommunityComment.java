package com.devloop.communitycomment.entity;

import com.devloop.common.Timestamped;
import com.devloop.community.entity.Community;
import com.devloop.communitycomment.dto.request.CommentSaveRequest;
import com.devloop.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class CommunityComment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="communityComment_id")
    private Long id;

    @NotNull
    @Column(name="communityComment_content",columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id",nullable = false)
    private Community community;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",nullable = false)
    private User user;

    //커뮤 댓글 생성자
    private CommunityComment(String content, Community community, User user){
        this.content=content;
        this.community=community;
        this.user=user;
    }

    public static CommunityComment of(String content, Community community, User user){
        return new CommunityComment(
                content,
                community,
                user
        );
    }

    //커뮤 댓글 수정 메서드
    public void updateContent(String newContent){
        this.content=newContent;
    }

}
