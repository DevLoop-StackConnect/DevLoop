package com.devloop.tutor.repository;

import com.devloop.tutor.entity.TutorRequest;
import com.devloop.common.enums.Approval;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TutorRequestRepository extends JpaRepository<TutorRequest, Long> {

    boolean existsByUserId(Long userId);

    @Query("SELECT tr FROM TutorRequest tr " +
            "WHERE tr.status = :status " +
            "ORDER BY tr.createdAt DESC")
    Optional<Page<TutorRequest>> findAllByStatus(
            Pageable pageable,
            @Param("status") Approval approval);

    Optional<TutorRequest> findByUserId(@Param("userId") Long userId);
}
