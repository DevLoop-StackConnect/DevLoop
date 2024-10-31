package com.devloop.lecturereview.repository;

import com.devloop.lecturereview.entity.LectureReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureReviewRepository extends JpaRepository<LectureReview,Long> {
}
