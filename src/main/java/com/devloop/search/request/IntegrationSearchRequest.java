package com.devloop.search.request;

import lombok.Getter;
import lombok.Setter;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Setter
@Getter
public class IntegrationSearchRequest {

    private String boardType;
    private String title;
    private String username;
    private String category;

    public String generateCacheKey() {
        return Stream.of(
                        boardType != null ? "type:" + boardType : "",
                        title != null ? "title:" + title : "",
                        username != null ? "user:" + username : "",
                        category != null ? "category:" + category : ""
                ).filter(key -> !key.isEmpty())
                .collect(Collectors.joining(":"));
    }
}