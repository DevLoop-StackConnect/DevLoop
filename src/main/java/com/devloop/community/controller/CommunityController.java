package com.devloop.community.controller;

import jakarta.validation.Valid;
import com.devloop.common.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.devloop.common.apipayload.ApiResponse;
import com.devloop.community.service.CommunityService;
import org.springframework.web.multipart.MultipartFile;
import com.devloop.community.request.CommunitySaveRequest;
import com.devloop.community.request.CommunityUpdateRequest;
import com.devloop.community.response.CommunitySaveResponse;
import com.devloop.community.response.CommunityDetailResponse;
import com.devloop.community.response.CommunitySimpleResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommunityController {
    private final CommunityService communityService;

    //게시글 작성
    @PostMapping("/v1/communities")
    public ApiResponse<CommunitySaveResponse> createCommunity(@AuthenticationPrincipal AuthUser authUser, @Valid @ModelAttribute CommunitySaveRequest communitySaveRequest, @RequestParam(value = "file", required = false) MultipartFile file) {
        return ApiResponse.ok(communityService.createCommunity(authUser, file, communitySaveRequest));
    }

    //게시글 다건 조회
    @GetMapping("/search/v1/communities")
    public ApiResponse<Page<CommunitySimpleResponse>> getCommunities(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(communityService.getCommunities(page, size));
    }

    //게시글 단건 조회
    @GetMapping("/search/v1/communities/{communityId}")
    public ApiResponse<CommunityDetailResponse> getCommunity(@PathVariable Long communityId) {
        return ApiResponse.ok(communityService.getCommunity(communityId));
    }

    //게시글 수정
    @PatchMapping("/v1/communities/{communityId}")
    public ApiResponse<CommunityDetailResponse> updateCommunity(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long communityId, @Valid @ModelAttribute CommunityUpdateRequest communityUpdateRequest, @RequestParam(value = "file", required = false) MultipartFile file) {
        return ApiResponse.ok(communityService.updateCommunity(authUser, communityId, communityUpdateRequest, file));
    }

    //게시글 삭제
    @DeleteMapping("/v1/communities/{communityId}")
    public ResponseEntity<Void> deleteCommunity(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long communityId) {
        communityService.deleteCommunity(authUser, communityId);
        return ResponseEntity.noContent().build();
    }
}
