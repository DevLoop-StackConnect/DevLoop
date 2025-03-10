package com.devloop.search.request;

import lombok.*;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Setter
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class IntegrationSearchRequest {

    private String boardType;
    private String title;
    private String content;
    private String username;
    private String category;

    public String generateCompositeKey() {
        return Stream.of(
                        title != null ? "title:" + title : "",
                        username != null ? "user:" + username : "",
                        content != null ? "content:" + content : "",
                        category != null ? "category:" + category : ""
                ).filter(key -> !key.isEmpty())
                .collect(Collectors.joining("|"));
    }
}