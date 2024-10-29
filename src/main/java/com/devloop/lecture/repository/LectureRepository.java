package com.devloop.lecture.repository;

import com.devloop.lecture.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureRepository extends JpaRepository<Lecture,Long> {
}
