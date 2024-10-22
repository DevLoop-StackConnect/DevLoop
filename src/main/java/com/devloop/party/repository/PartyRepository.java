package com.devloop.party.repository;

import com.devloop.party.entity.Party;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyRepository extends JpaRepository<Party,Long>, PartyQueryRepository {
}
