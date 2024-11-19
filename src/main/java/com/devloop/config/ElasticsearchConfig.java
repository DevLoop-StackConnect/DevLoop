package com.devloop.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchCustomConversions;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.lang.NonNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;

@Slf4j
@Configuration
@EnableElasticsearchRepositories(
        basePackages = {
                "com.devloop.party.repository.elasticsearch",
                "com.devloop.lecture.repository.elasticsearch",
                "com.devloop.community.repository.elasticsearch",
                "com.devloop.pwt.repository.elasticsearch"
        }
)
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Value("${spring.elasticsearch.uris}")
    private String elasticsearchUrl;

    @Value("${spring.elasticsearch.username}")
    private String username;

    @Value("${spring.elasticsearch.password}")
    private String password;

    @Override
    @NonNull
    public ClientConfiguration clientConfiguration() {
        log.info("Connecting to Elasticsearch at: {}", elasticsearchUrl);
        return ClientConfiguration.builder()
                .connectedTo(elasticsearchUrl.replace("http://", ""))
                .withBasicAuth(username, password)
                .withSocketTimeout(60000)  // 60초
                .withConnectTimeout(5000)  // 5초
                .build();
    }

    @Override
    @NonNull
    public ElasticsearchCustomConversions elasticsearchCustomConversions() {
        return new ElasticsearchCustomConversions(
                Arrays.asList(
                        new LocalDateTimeToLongConverter(),
                        new LongToLocalDateTimeConverter()
                )
        );
    }

    @WritingConverter
    static class LocalDateTimeToLongConverter implements Converter<LocalDateTime, Long> {
        @Override
        @NonNull
        public Long convert(LocalDateTime source) {
            return source.toInstant(ZoneOffset.UTC).toEpochMilli();
        }
    }

    @ReadingConverter
    static class LongToLocalDateTimeConverter implements Converter<Long, LocalDateTime> {
        @Override
        @NonNull
        public LocalDateTime convert(Long source) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(source), ZoneId.systemDefault());
        }
    }
}