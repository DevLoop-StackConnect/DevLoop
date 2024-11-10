package com.devloop.search.controller;

import com.devloop.common.apipayload.ApiResponse;
import com.devloop.search.request.IntegrationSearchRequest;
import com.devloop.search.response.IntegratedSearchPreview;
import com.devloop.search.response.IntegrationSearchResponse;
import com.devloop.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SearchController {

    private final SearchService searchService;

    @PostMapping("/v1/main/search/preview")
    @PreAuthorize("permitAll()")
    public ApiResponse<IntegratedSearchPreview> previewSearch(
            @RequestBody IntegrationSearchRequest request) {
        log.info("검색 요청 받음: {}", request);
        return ApiResponse.ok(searchService.integratedSearchPreview(request));
    }

    @GetMapping("/v1/main/search/{category}")
    @PreAuthorize("permitAll()")
    public ApiResponse<Page<IntegrationSearchResponse>> searchByCategory(
            @PathVariable String category,
            @RequestBody IntegrationSearchRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(searchService.searchByCategory(request, category, page, size));
    }

//    @GetMapping("/v1/main/search/ranking")
//    @PreAuthorize("permitAll()")
//    public ApiResponse<Set<ZSetOperations.TypedTuple<String>>> getRankingKeyword(){
//        return ApiResponse.ok(searchService.getTopSearchKeywords());
//    }
}