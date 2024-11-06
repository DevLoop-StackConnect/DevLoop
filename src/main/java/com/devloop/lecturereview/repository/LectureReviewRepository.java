package com.devloop.lecturereview.repository;

import com.devloop.lecturereview.entity.LectureReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureReviewRepository extends JpaRepository<LectureReview, Long> {
    Page<LectureReview> findByLectureId(Long id, Pageable pageable);
}
