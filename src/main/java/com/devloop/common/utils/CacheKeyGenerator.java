package com.devloop.common.utils;

import com.devloop.search.request.IntegrationSearchRequest;
import org.springframework.util.StringUtils;

public class CacheKeyGenerator {

    public static String generateKey(IntegrationSearchRequest request) {
        StringBuilder keyBuilder = new StringBuilder();

        if (StringUtils.hasText(request.getTitle())) {
            keyBuilder.append("title:").append(request.getTitle());
        }
        if (StringUtils.hasText(request.getUsername())) {
            keyBuilder.append(":user:").append(request.getUsername());
        }
        if (StringUtils.hasText(request.getCategory())) {
            keyBuilder.append(":category:").append(request.getCategory());
        }
        return keyBuilder.length() > 0 ? keyBuilder.toString() : "all";
    }

    public static String generateCategoryKey(String category, int page, IntegrationSearchRequest request) {
        return String.format("category:%s:page:%d:%s",
                category, page, generateKey(request));
    }
}


