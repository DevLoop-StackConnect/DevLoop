package com.devloop.common.utils;

import com.devloop.search.response.IntegrationSearchResponse;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
@JsonDeserialize(using = CacheablePageDeserializer.class)
public class CacheablePage<T extends IntegrationSearchResponse> implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
    private final List<T> content;

    @JsonProperty
    private final Long totalElements;
    @JsonProperty
    private final int pageNumber;
    @JsonProperty
    private final int pageSize;
    @JsonProperty
    private final String sortDirection;
    @JsonProperty
    private final String sortProperty;

    @Builder
    @JsonCreator
    @SuppressWarnings("unchecked")
    public CacheablePage(
            @JsonProperty("content") List<T> content,
            @JsonProperty("totalElements") Long totalElements,
            @JsonProperty("pageNumber") int pageNumber,
            @JsonProperty("pageSize") int pageSize,
            @JsonProperty("sortDirection") String sortDirection,
            @JsonProperty("sortProperty") String sortProperty) {

        this.content = content.stream()
                .map(item -> {
                    if (item.getId() == null && item.getTitle() != null) {
                        try {
                            String title = item.getTitle();
                            Long id = Long.parseLong(title.replaceAll("\\D+", ""));
                            return (T) IntegrationSearchResponse.builder()
                                    .id(id)
                                    .boardType(item.getBoardType())
                                    .title(item.getTitle())
                                    .content(item.getContent())
                                    .category(item.getCategory())
                                    .username(item.getUsername())
                                    .createdAt(item.getCreatedAt() != null ? item.getCreatedAt() : LocalDateTime.now())
                                    .score(item.getScore())
                                    .build();
                        } catch (Exception e) {
                            return item;
                        }
                    }
                    return item;
                })
                .collect(Collectors.toList());

        this.totalElements = totalElements;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.sortDirection = sortDirection;
        this.sortProperty = sortProperty;
    }

    public CacheablePage(Page<T> page) {
        List<T> pageContent = page.getContent();
        this.content = pageContent.stream()
                .map(item -> {
                    if (item.getId() == null && item.getTitle() != null) {
                        try {
                            String title = item.getTitle();
                            Long id = Long.parseLong(title.replaceAll("\\D+", ""));
                            return (T) IntegrationSearchResponse.builder()
                                    .id(id)
                                    .boardType(item.getBoardType())
                                    .title(item.getTitle())
                                    .content(item.getContent())
                                    .category(item.getCategory())
                                    .username(item.getUsername())
                                    .createdAt(item.getCreatedAt() != null ? item.getCreatedAt() : LocalDateTime.now())
                                    .score(item.getScore())
                                    .build();
                        } catch (Exception e) {
                            return item;
                        }
                    }
                    return item;
                })
                .collect(Collectors.toList());

        this.totalElements = page.getTotalElements();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();

        if (page.getSort().isSorted()) {
            Sort.Order order = page.getSort().iterator().next();
            this.sortDirection = order.getDirection().name();
            this.sortProperty = order.getProperty();
        } else {
            this.sortDirection = Sort.Direction.DESC.name();
            this.sortProperty = "createdAt";
        }
    }

    public Page<T> toPage() {
        return new PageImpl<>(
                content,
                PageRequest.of(
                        pageNumber,
                        pageSize,
                        Sort.Direction.valueOf(sortDirection),
                        sortProperty
                ),
                totalElements
        );
    }

    public static <T extends IntegrationSearchResponse> CacheablePage<T> of(Page<T> page) {
        return new CacheablePage<>(page);
    }
}