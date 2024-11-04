package com.devloop.lecture.repository;

import com.devloop.lecture.entity.LectureVideo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureVideoRepository extends JpaRepository<LectureVideo,Long> {
    List<LectureVideo> findAllByLectureId(Long lectureId);
}
