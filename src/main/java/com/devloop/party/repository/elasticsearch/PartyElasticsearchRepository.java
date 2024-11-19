package com.devloop.party.repository.elasticsearch;

import com.devloop.party.entity.Party;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PartyElasticsearchRepository extends ElasticsearchRepository<Party, Long> {
}
