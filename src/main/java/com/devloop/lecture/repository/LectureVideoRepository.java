package com.devloop.lecture.repository;

import com.devloop.lecture.entity.LectureVideo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureVideoRepository extends JpaRepository<LectureVideo,Long> {
}
