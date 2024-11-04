package com.devloop.lecture.entity;

import com.devloop.common.enums.Approval;
import com.devloop.common.enums.Category;
import com.devloop.lecture.request.SaveLectureRequest;
import com.devloop.lecture.request.UpdateLectureRequest;
import com.devloop.lecturereview.entity.LectureReview;
import com.devloop.product.entity.Product;
import com.devloop.pwt.enums.Level;
import com.devloop.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class Lecture extends Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Column(length = 20)
    private String recommend;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Category category;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Level level;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Approval approval=Approval.WAITE;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<LectureVideo> lectureVideos;

    @OneToMany(mappedBy = "lecture",  cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<LectureReview> lectureReviews;

    private Lecture(String title, String description, String recommend, Category category, Level level, BigDecimal price, User user){
        super(title,price);
        this.description=description;
        this.recommend=recommend;
        this.category=category;
        this.level=level;
        this.user=user;
    }

    public static Lecture from(SaveLectureRequest request, User user){
        return new Lecture(
                request.getTitle(),
                request.getDescription(),
                request.getRecommend(),
                Category.of(request.getCategory()),
                Level.of(request.getLevel()),
                request.getPrice(),
                user
        );
    }

    public void update(UpdateLectureRequest request){
        update(request.getTitle(),request.getPrice());
        this.description= request.getDescription();
        this.recommend= request.getRecommend();
        this.category=Category.of(request.getCategory());
        this.level=Level.of(request.getLevel());
    }

    public void changeApproval(Approval approval) {
        this.approval = approval;
    }
}
