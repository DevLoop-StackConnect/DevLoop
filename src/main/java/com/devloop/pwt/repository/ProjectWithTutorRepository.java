package com.devloop.pwt.repository;

import com.devloop.common.apipayload.dto.ProjectWithTutorResponseDto;
import com.devloop.common.enums.Approval;
import com.devloop.pwt.entity.ProjectWithTutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProjectWithTutorRepository extends JpaRepository<ProjectWithTutor, Long> {

    @Query(
            "SELECT new com.devloop.common.apipayload.dto.ProjectWithTutorResponseDto(p.id, p.title, p.price, p.status, p.deadline, p.maxParticipants, p.level, p.user)" +
            "FROM ProjectWithTutor p " +
            "WHERE p.approval = :approval " +
            "ORDER BY p.modifiedAt DESC"
    )
    Optional<Page<ProjectWithTutorResponseDto>> findAllApprovedProjectWithTutor(@Param("approval") Approval approval, Pageable pageable);
}
