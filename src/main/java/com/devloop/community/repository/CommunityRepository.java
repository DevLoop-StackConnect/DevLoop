package com.devloop.community.repository;

import com.devloop.common.apipayload.dto.CommunitySimpleResponseDto;
import com.devloop.community.entity.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommunityRepository extends JpaRepository<Community, Long>, JpaSpecificationExecutor<Community> {

    @Query("select Community from Community c where c.user.id =:userId")
    Optional<Community> findByUserId(@Param("userId") Long userId);
    @Query("SELECT new com.devloop.common.apipayload.dto.CommunitySimpleResponseDto(c.id, c.title, c.resolveStatus, c.category) " +
            "FROM Community c " +
            "ORDER BY c.createdAt DESC")
    Optional<Page<CommunitySimpleResponseDto>>findAllSimple(Pageable pageable);

    Optional<Community> findById(Long communityId);
}
