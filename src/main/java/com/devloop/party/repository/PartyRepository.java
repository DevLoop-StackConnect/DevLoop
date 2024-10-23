package com.devloop.party.repository;

import com.devloop.party.entity.Party;
import com.devloop.partycomment.entity.PartyComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyRepository extends JpaRepository<Party,Long>{

    Page<Party> findByTitleContaining(String title, PageRequest pageable);

}
