package com.devloop.lecture.repository;

import com.devloop.lecture.entity.Lecture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureRepository extends JpaRepository<Lecture,Long> {
    Page<Lecture> findByTitleContaining(String title, PageRequest pageable);
}
