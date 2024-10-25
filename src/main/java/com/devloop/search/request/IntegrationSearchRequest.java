package com.devloop.search.request;

import lombok.Getter;

@Getter
public class IntegrationSearchRequest {

    private String boardType;
    private String title;
    private String username;
    private String category;
    //강의 목록 추가 가능
}
