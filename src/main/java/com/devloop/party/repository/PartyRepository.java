package com.devloop.party.repository;

import com.devloop.party.entity.Party;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PartyRepository extends JpaRepository<Party,Long>, JpaSpecificationExecutor<Party> {

    Page<Party> findByTitleContaining(String title, PageRequest pageable);

    @Query("SELECT p FROM Party p WHERE p.user.id =:userId")
    Optional<Party> findByUserId(@Param("userId") Long userId);
}
