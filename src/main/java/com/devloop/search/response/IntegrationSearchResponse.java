package com.devloop.search.response;

import lombok.Getter;

@Getter
public class IntegrationSearchResponse {

    private final String boardType;
    private final Object data;

    private IntegrationSearchResponse(String boardType, Object data) {
        this.boardType = boardType;
        this.data = data;
    }

    public static IntegrationSearchResponse from(String boardType, Object data){
        return new IntegrationSearchResponse(boardType, data);
    }
}
