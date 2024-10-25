package com.devloop.common.utils;

import com.devloop.common.enums.BoardType;
import com.devloop.search.response.IntegrationSearchResponse;

import java.util.List;

public class SearchResponseUtil {

    public static <T>List<IntegrationSearchResponse> wrapResponse(BoardType boardType, List<T> posts){
        return posts.stream()
                .map(post -> IntegrationSearchResponse.of(boardType.name().toLowerCase(), post))
                .toList();
    }
}
