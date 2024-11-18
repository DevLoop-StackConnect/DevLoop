package com.devloop.lecture.repository.jpa;

import com.devloop.common.enums.Approval;
import com.devloop.lecture.entity.Lecture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface LectureRepository extends JpaRepository<Lecture,Long>, QuerydslPredicateExecutor<Lecture> {
    Page<Lecture> findByTitleContainingAndApproval(String title, Approval approval, PageRequest pageable);

    Page<Lecture> findByApproval(Approval approval, PageRequest pageable);

    // 기본 조회 - user만 함께 조회
    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT l FROM Lecture l")
    Page<Lecture> findAll(Pageable pageable);

    // videos만 조회
    @EntityGraph(attributePaths = {"user", "lectureVideos"})
    @Query("SELECT l FROM Lecture l WHERE l.id IN :ids")
    List<Lecture> findWithVideosByIds(@Param("ids") Collection<Long> ids);

    // reviews만 조회
    @EntityGraph(attributePaths = {"user", "lectureReviews"})
    @Query("SELECT l FROM Lecture l WHERE l.id IN :ids")
    List<Lecture> findWithReviewsByIds(@Param("ids") Collection<Long> ids);
}
