package com.devloop.search.controller;

import com.devloop.common.apipayload.ApiResponse;
import com.devloop.common.utils.CacheablePage;
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
    public ApiResponse<IntegratedSearchPreview> previewSearch(
            @RequestBody IntegrationSearchRequest request) {
        IntegratedSearchPreview result = searchService.integratedSearchPreview(request);
        return ApiResponse.ok(result);
    }

    @PostMapping("/v1/main/search/detail/{boardType}")
    @PreAuthorize("permitAll()")
    public ApiResponse<Page<IntegrationSearchResponse>> searchDetail(
            @PathVariable String boardType,
            @RequestBody IntegrationSearchRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        CacheablePage<IntegrationSearchResponse> result = searchService.searchByBoardType(request, boardType, page, size);
        return ApiResponse.ok(result.toPage());
    }

    @GetMapping("/v1/main/search/ranking")
    @PreAuthorize("permitAll()")
    public ApiResponse<Set<ZSetOperations.TypedTuple<String>>> getRankingKeyword() {
        return ApiResponse.ok(searchService.getTopSearchKeywords());
    }
}