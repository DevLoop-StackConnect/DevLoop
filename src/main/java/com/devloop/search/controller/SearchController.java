package com.devloop.search.controller;

import com.devloop.common.apipayload.ApiResponse;
import com.devloop.search.request.IntegrationSearchRequest;
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

    @GetMapping("/v1/main/search")
    public ApiResponse<Page<IntegrationSearchResponse>> integrationSearch(
            @RequestBody IntegrationSearchRequest integrationSearchRequest,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size){
        Page<IntegrationSearchResponse> search = searchService.integrationSearch(integrationSearchRequest,page, size);
        return ApiResponse.ok(search);
    }
}
