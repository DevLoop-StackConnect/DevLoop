package com.devloop.search.controller;

import com.devloop.common.apipayload.ApiResponse;
import com.devloop.search.request.IntegrationSearchRequest;
import com.devloop.search.response.IntegratedSearchPreview;
import com.devloop.search.response.IntegrationSearchResponse;
import com.devloop.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/v1/main/search/preview")
    public ApiResponse<IntegratedSearchPreview> previewSearch(
            @RequestBody IntegrationSearchRequest request) {
        return ApiResponse.ok(searchService.integratedSearchPreview(request));
    }

    @GetMapping("/v1/main/search/{category}")
    public ApiResponse<Page<IntegrationSearchResponse>> searchByCategory(
            @PathVariable String category,
            @RequestBody IntegrationSearchRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(searchService.searchByCategory(request, category, page, size));
    }
}