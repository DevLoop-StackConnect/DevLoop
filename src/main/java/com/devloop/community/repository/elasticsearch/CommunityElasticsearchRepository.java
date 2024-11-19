package com.devloop.community.repository.elasticsearch;

import com.devloop.community.entity.Community;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CommunityElasticsearchRepository extends ElasticsearchRepository<Community, Long> {
}
