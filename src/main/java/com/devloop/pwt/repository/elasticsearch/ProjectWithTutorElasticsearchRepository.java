package com.devloop.pwt.repository.elasticsearch;

import com.devloop.pwt.entity.ProjectWithTutor;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProjectWithTutorElasticsearchRepository extends ElasticsearchRepository<ProjectWithTutor, Long> {
}
