package com.devloop.party.repository.jpa;

import com.devloop.party.entity.Party;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PartyRepository extends JpaRepository<Party, Long>, QuerydslPredicateExecutor<Party> {

    Page<Party> findByTitleContaining(String title, PageRequest pageable);

    @Query("SELECT p FROM Party p WHERE p.user.id =:userId")
    List<Party> findAllByUserId(@Param("userId") Long id);

    @EntityGraph(attributePaths = {"comments", "user"})
    Page<Party> findAll(Pageable pageable);
}
