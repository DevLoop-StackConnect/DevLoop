package com.devloop.community.repository;

import com.devloop.community.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CommunityRepository extends JpaRepository<Community,Long>, JpaSpecificationExecutor<Community> {
    @Query("select Community from Community c where User.id =:userId")
    Optional<Community> findByUserId(Long userId);
}
