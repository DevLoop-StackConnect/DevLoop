package com.devloop.search.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class IntegrationSearchRequest {

    private String boardType;
    private String title;
    private String content;
    private String username;
    private String category;
    private String lecture;

    public String generateCompositeKey() {
        return Stream.of(
                        title != null ? "title:" + title : "",
                        username != null ? "user:" + username : "",
                        content != null ? "content:" + content : "",
                        category != null ? "category:" + category : "",
                        lecture != null ? "lecture:" + lecture: ""
                ).filter(key -> !key.isEmpty())
                .collect(Collectors.joining("|"));
    }
}