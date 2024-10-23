package com.devloop.community.entity;

import com.devloop.common.Timestamped;
import com.devloop.common.enums.Category;
import com.devloop.community.dto.request.CommunitySaveRequest;
import com.devloop.communitycomment.entity.CommunityComment;
import com.devloop.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Community extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="community_id")
    private Long id;

    @NotNull
    @Column(name="title")
    private String title;

    @NotNull
    @Column(name="content",columnDefinition = "TEXT")
    private String content;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Category category;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ResolveStatus resolveStatus;

    @OneToMany(mappedBy = "community",fetch = FetchType.LAZY)
    private List<CommunityComment> communityComments = new ArrayList<>();

    //!!!!!!!!!!원투매니로 이미지 첨부파일 연관관계 맺어야함

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    //커뮤니티 글 생성자
    private Community(String title, String content,ResolveStatus resolveStatus,Category category,User user){
        this.title=title;
        this.content=content;
        this.resolveStatus=resolveStatus;
        this.category=category;
        this.user=user;
    }

    public static Community from(CommunitySaveRequest communitySaveRequest, User user){
        return new Community(
                communitySaveRequest.getTitle(),
                communitySaveRequest.getContent(),
                communitySaveRequest.getStatus(),
                communitySaveRequest.getCategory(),
                user
        );
    }

    //커뮤니티 글 수정 메서드
    public void updateCommunity(String title, String content,ResolveStatus resolveStatus,Category category){
        this.title=title;
        this.content=content;
        this.resolveStatus=resolveStatus;
        this.category=category;
    }






}
