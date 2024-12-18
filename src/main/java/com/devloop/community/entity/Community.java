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
import org.springframework.data.elasticsearch.annotations.*;

@Entity
@Getter
@Builder
@Document(indexName = "community")
@Setting(settingPath = "/elasticsearch/setting.json")
@Mapping(mappingPath = "/elasticsearch/community-mapping.json")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Community extends Timestamped {

    @Id
    @Column(name = "community_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Field(type = FieldType.Keyword, name = "board_type")
    private BoardType boardType = BoardType.COMMUNITY;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Category category = Category.ETC;

    @Builder.Default
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ResolveStatus resolveStatus = ResolveStatus.UNSOLVED; //기본값 필드로 설정

    @Builder.Default
    @OneToMany(mappedBy = "community", fetch = FetchType.LAZY)
    private List<CommunityComment> communityComments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @Field(type = FieldType.Object, includeInParent = true)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //커뮤니티 글 생성자
    private Community(String title, String content, Category category, User user) {
        this.title = title;
        this.content = content;
        this.category = category != null ? category : Category.ETC;
        this.user = user;
        this.resolveStatus = ResolveStatus.UNSOLVED;
        this.boardType = BoardType.COMMUNITY;
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
