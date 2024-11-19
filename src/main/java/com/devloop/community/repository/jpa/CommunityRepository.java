package com.devloop.community.repository.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import com.devloop.community.entity.Community;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import com.devloop.common.apipayload.dto.CommunitySimpleResponseDto;

public interface CommunityRepository extends JpaRepository<Community, Long>, QuerydslPredicateExecutor<Community> {

    @Query("select Community from Community c where c.user.id =:userId")
    Optional<Community> findByUserId(@Param("userId") Long userId);

    @Query("SELECT new com.devloop.common.apipayload.dto.CommunitySimpleResponseDto(c.id, c.title, c.resolveStatus, c.category) " +
            "FROM Community c " +
            "ORDER BY c.createdAt DESC")
    Page<CommunitySimpleResponseDto> findAllSimple(Pageable pageable);

    Optional<Community> findById(Long communityId);

    @Query("select c from Community c where c.user.id = :userId")
    List<Community> findAllByUserId(@Param("userId") Long id);

    @EntityGraph(attributePaths = {"communityComments", "user"})
    Page<Community> findAll(Pageable pageable);
}
