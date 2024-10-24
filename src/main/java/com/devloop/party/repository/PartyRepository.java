package com.devloop.party.repository;

import com.devloop.party.entity.Party;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PartyRepository extends JpaRepository<Party,Long>, JpaSpecificationExecutor<Party> {

    Page<Party> findByTitleContaining(String title, PageRequest pageable);

}
