package com.devloop.party.repository;

import com.devloop.party.entity.Party;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PartyRepository extends JpaRepository<Party, Long>, JpaSpecificationExecutor<Party> {

    Page<Party> findByTitleContaining(String title, PageRequest pageable);

    //사용중 아님 확인 필요
    @Query("SELECT p FROM Party p JOIN FETCH p.user WHERE p.id = :partyId")
    Optional<Party> findByIdWithUser(@Param("partyId") Long partyId);

    @Query("SELECT p FROM Party p WHERE p.user.id =:userId")
    Optional<List<Party>> findAllByUserId(@Param("userId") Long id);
}
