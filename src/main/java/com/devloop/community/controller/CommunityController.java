package com.devloop.community.controller;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.ApiResponse;
import com.devloop.community.dto.request.CommunitySaveRequest;
import com.devloop.community.dto.request.CommunityUpdateRequest;
import com.devloop.community.dto.response.CommunityDetailResponse;
import com.devloop.community.dto.response.CommunitySaveResponse;
import com.devloop.community.dto.response.CommunitySimpleResponse;
import com.devloop.community.service.CommunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommunityController {
    private final CommunityService communityService;

    //게시글 작성
    @PostMapping("/v1/communities")
    public ApiResponse<CommunitySaveResponse> createCommunity(@AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody CommunitySaveRequest communitySaveRequest){
        CommunitySaveResponse communitySaveResponse = communityService.createCommunity(authUser,communitySaveRequest);
        return ApiResponse.ok(communitySaveResponse);
    }

    //게시글 다건 조회
    @GetMapping("/v1/communities")
    public ApiResponse<Page<CommunitySimpleResponse>> getCommunities(@RequestParam(defaultValue = "0")int page,@RequestParam(defaultValue = "10")int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<CommunitySimpleResponse> communitySimpleResponse = communityService.getCommunities(pageable);
        return ApiResponse.ok(communitySimpleResponse);
    }

    //게시글 단건 조회
    @GetMapping("/v1/communities/{communityId}")
    public ApiResponse<CommunityDetailResponse> getCommunity(@PathVariable Long communityId){
        CommunityDetailResponse communityDetailResponse = communityService.getCommunity(communityId);
        return ApiResponse.ok(communityDetailResponse);
    }

    //게시글 수정
    @PatchMapping("/v1/communities/{communityId}")
    public  ApiResponse<CommunityDetailResponse> updateCommunity(@PathVariable Long communityId, @Valid @RequestBody CommunityUpdateRequest communityUpdateRequest){
        CommunityDetailResponse communityDetailResponse = communityService.updateCommunity(communityId,communityUpdateRequest);
        return ApiResponse.ok(communityDetailResponse);
    }

    //게시글 삭제
    @DeleteMapping("/v1/communities/{communityId}")
    public ApiResponse<Void> deleteCommunity(@PathVariable Long communityId){
        communityService.deleteCommunity(communityId);
        return ApiResponse.ok(null);
    }
}
