package com.devloop.lecture.entity;

import com.devloop.common.enums.Approval;
import com.devloop.common.enums.BoardType;
import com.devloop.common.enums.Category;
import com.devloop.lecture.request.SaveLectureRequest;
import com.devloop.lecture.request.UpdateLectureRequest;
import com.devloop.lecturereview.entity.LectureReview;
import com.devloop.product.entity.Product;
import com.devloop.pwt.enums.Level;
import com.devloop.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Lecture extends Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(length = 20, nullable = false)
    private String recommend;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private BoardType boardType = BoardType.LECTURE;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Level level;

    @Enumerated(EnumType.STRING)
    private Approval approval = Approval.WAITE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<LectureVideo> lectureVideos;

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<LectureReview> lectureReviews;

    private Lecture(String title, String description, String recommend, Category category, Level level, BigDecimal price, User user) {
        super(title, price);
        this.description = description;
        this.recommend = recommend;
        this.category = category;
        this.level = level;
        this.user = user;
    }
    public static Lecture from(SaveLectureRequest request, User user) {
        return new Lecture(
                request.getTitle(),
                request.getDescription(),
                request.getRecommend(),
                request.getCategory(),
                request.getLevel(),
                request.getPrice(),
                user
        );
    }
    public void update(UpdateLectureRequest request) {
        update(request.getTitle(), request.getPrice());
        this.description = request.getDescription();
        this.recommend = request.getRecommend();
        this.category = request.getCategory();
        this.level = request.getLevel();
    }
    public void changeApproval(Approval approval) {
        this.approval = approval;
    }
}
