package com.devloop.lecturereview.entity;

import com.devloop.common.Timestamped;
import com.devloop.lecture.entity.Lecture;
import com.devloop.lecturereview.request.SaveLectureReviewRequest;
import com.devloop.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class LectureReview extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(length = 255)
    private String review;

    @NotNull
    private Integer rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="lecture_id")
    private Lecture lecture;

    private LectureReview(String review,Integer rating,User user,Lecture lecture){
        this.review=review;
        this.rating=rating;
        this.user=user;
        this.lecture=lecture;
    }

    public static LectureReview from(SaveLectureReviewRequest request, User user, Lecture lecture){
        return new LectureReview(
                request.getReview(),
                request.getRating(),
                user,
                lecture
        );
    }

    public void update(SaveLectureReviewRequest request){
        this.review=request.getReview();
        this.rating=request.getRating();
    }
}
