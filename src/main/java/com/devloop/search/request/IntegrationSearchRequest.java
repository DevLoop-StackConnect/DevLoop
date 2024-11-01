package com.devloop.search.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IntegrationSearchRequest {

    private String boardType;
    private String title;
    private String username;
    private String category;

    public String generateCacheKey(){
        StringBuilder key = new StringBuilder();
        if(boardType != null) key.append(":type").append(boardType);
        if(title != null) key.append(":title").append(title);
        if(username != null) key.append(":uesr").append(username);
        if(category != null) key.append(":category").append(category);
        return key.toString();
    }
}
