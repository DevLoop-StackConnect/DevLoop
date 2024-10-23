package com.devloop.community.controller;

import com.devloop.common.AuthUser;
import com.devloop.community.dto.request.CommunitySaveRequest;
import com.devloop.community.dto.response.CommunityDetailResponse;
import com.devloop.community.dto.response.CommunitySaveResponse;
import com.devloop.community.dto.response.CommunitySimpleResponse;
import com.devloop.community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/communities")
public class CommunityController {
    private final CommunityService communityService;

    //게시글 작성
    @PostMapping
    public ResponseEntity<CommunitySaveResponse> createCommunity(@AuthenticationPrincipal AuthUser authUser, @RequestBody CommunitySaveRequest communitySaveRequest){
        System.out.println("표시");
        CommunitySaveResponse communitySaveResponse = communityService.createCommunity(authUser,communitySaveRequest);
        return ResponseEntity.ok(communitySaveResponse);
    }

    //게시글 다건 조회
    @GetMapping
    public ResponseEntity<Page<CommunitySimpleResponse>> getCommunities(@RequestParam(defaultValue = "0")int page,@RequestParam(defaultValue = "10")int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<CommunitySimpleResponse> communitySimpleResponse = communityService.getCommunities(pageable);
        return ResponseEntity.ok(communitySimpleResponse);
    }

    //게시글 단건 조회
    @GetMapping("/{communityId}")
    public ResponseEntity<CommunityDetailResponse> getCommunity(@PathVariable Long communityId){
        CommunityDetailResponse communityDetailResponse = communityService.getCommunity(communityId);
        return ResponseEntity.ok(communityDetailResponse);
    }
}
