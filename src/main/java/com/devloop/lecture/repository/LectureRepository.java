package com.devloop.lecture.repository;

import com.devloop.common.enums.Approval;
import com.devloop.lecture.entity.Lecture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LectureRepository extends JpaRepository<Lecture,Long> , JpaSpecificationExecutor<Lecture> {
    Page<Lecture> findByTitleContainingAndApproval(String title, Approval approval,PageRequest pageable);

    Page<Lecture> findByApproval(Approval approval, PageRequest pageable);
}
