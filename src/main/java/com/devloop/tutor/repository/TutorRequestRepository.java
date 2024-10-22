package com.devloop.tutor.repository;

import com.devloop.tutor.entity.TutorRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TutorRequestRepository extends JpaRepository<TutorRequest, Long> {

}
