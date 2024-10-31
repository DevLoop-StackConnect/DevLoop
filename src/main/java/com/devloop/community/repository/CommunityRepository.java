package com.devloop.community.repository;

import com.devloop.community.dto.response.CommunitySimpleResponse;
import com.devloop.community.entity.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommunityRepository extends JpaRepository<Community,Long>, JpaSpecificationExecutor<Community> {

    @Query("SELECT new com.devloop.community.dto.response.CommunitySimpleResponse(c.id, c.title, c.resolveStatus, c.category) " +
            "FROM Community c " +
            "ORDER BY c.createdAt DESC")
    Page<CommunitySimpleResponse> findAllSimple(Pageable pageable);

    Optional<Community> findById(Long communityId);

    @Query("SELECT c FROM Community c JOIN FETCH c.user WHERE c.id = :communityId")
    Optional<Community> findByIdWithUser(@Param("communityId") Long communityId);

    @Query("select c from Community c where c.user.id = :userId")
    Optional<List<Community>> findAllByUserId(@Param("userId") Long id);



}
