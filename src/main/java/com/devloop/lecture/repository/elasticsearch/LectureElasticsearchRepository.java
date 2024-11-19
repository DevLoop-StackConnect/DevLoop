package com.devloop.lecture.repository.elasticsearch;

import com.devloop.lecture.entity.Lecture;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface LectureElasticsearchRepository extends ElasticsearchRepository<Lecture, Long> {
}
