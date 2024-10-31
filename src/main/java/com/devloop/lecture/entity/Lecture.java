package com.devloop.lecture.entity;

import com.devloop.common.Timestamped;
import com.devloop.common.enums.Approval;
import com.devloop.common.enums.Category;
import com.devloop.lecture.request.SaveLectureRequest;
import com.devloop.lecture.request.UpdateLectureRequest;
import com.devloop.pwt.enums.Level;
import com.devloop.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Lecture extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(length = 255)
    private String title;

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
    private Integer price;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Approval approval=Approval.WAITE;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    private Lecture(String title, String description, String recommend,Category category, Level level,Integer price,User user){
        this.title=title;
        this.description=description;
        this.recommend=recommend;
        this.category=category;
        this.level=level;
        this.price=price;
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
        this.title= request.getTitle();
        this.description= request.getDescription();
        this.recommend= request.getRecommend();
        this.category=Category.of(request.getCategory());
        this.level=Level.of(request.getLevel());
        this.price=request.getPrice();
    }

    public void changeApproval(Approval approval) {
        this.approval = approval;
    }
}
