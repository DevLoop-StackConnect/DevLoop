package com.devloop.community.repository;

import com.devloop.community.dto.response.CommunitySimpleResponse;
import com.devloop.community.entity.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface CommunityRepository extends JpaRepository<Community, Long>, JpaSpecificationExecutor<Community> {
    @Query("SELECT new com.devloop.community.dto.response.CommunitySimpleResponse(c.id, c.title, c.resolveStatus, c.category) " +
            "FROM Community c " +
            "ORDER BY c.createdAt DESC")
    Page<CommunitySimpleResponse> findAllSimple(Pageable pageable);
}
