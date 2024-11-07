package com.devloop.lecturereview.entity;

import com.devloop.common.Timestamped;
import com.devloop.lecture.entity.Lecture;
import com.devloop.lecturereview.request.SaveLectureReviewRequest;
import com.devloop.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureReview extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false)
    private String review;

    @Column(nullable = false)
    private Integer rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    private LectureReview(String review, Integer rating, User user, Lecture lecture) {
        this.review = review;
        this.rating = rating;
        this.user = user;
        this.lecture = lecture;
    }

    public static LectureReview from(SaveLectureReviewRequest request, User user, Lecture lecture) {
        return new LectureReview(
                request.getReview(),
                request.getRating(),
                user,
                lecture
        );
    }

    public void update(SaveLectureReviewRequest request) {
        this.review = request.getReview();
        this.rating = request.getRating();
    }
}
