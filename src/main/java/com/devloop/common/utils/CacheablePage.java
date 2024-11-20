package com.devloop.common.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.List;

@Getter

public class CacheablePage<T> implements Serializable {
    private final List<T> content;
    private final Long totalElements;
    private final int pageNumber;
    private final int pageSize;
    private final String sortDirection;
    private final String sortProperty;

    public CacheablePage(Page<T> page){
        this.content = page.getContent();
        this.totalElements = page.getTotalElements();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();

        if(page.getSort().isSorted()){
            Sort.Order order =page.getSort().iterator().next();
            this.sortDirection = order.getDirection().name();
            this.sortProperty = order.getProperty();
        } else {
            this.sortDirection = Sort.Direction.DESC.name();
            this.sortProperty = "createdAt";
        }
    }
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public CacheablePage(
            @JsonProperty("content") List<T> content,
            @JsonProperty("totalElements") long totalElements,
            @JsonProperty("pageNumber") int pageNumber,
            @JsonProperty("pageSize") int pageSize,
            @JsonProperty("sortDirection") String sortDirection,
            @JsonProperty("sortProperty") String sortProperty) {
        this.content = content;
        this.totalElements = totalElements;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.sortDirection = sortDirection;
        this.sortProperty = sortProperty;
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
}
