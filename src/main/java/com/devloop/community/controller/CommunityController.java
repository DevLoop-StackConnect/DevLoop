package com.devloop.community.controller;

import com.devloop.common.AuthUser;
import com.devloop.community.dto.request.CommunitySaveRequest;
import com.devloop.community.dto.response.CommunitySaveResponse;
import com.devloop.community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/communities")
public class CommunityController {
    private final CommunityService communityService;

    //게시글 작성
    @PostMapping("/v1")
    public ResponseEntity<CommunitySaveResponse> createCommunity(@AuthenticationPrincipal AuthUser authUser, @RequestBody CommunitySaveRequest communitySaveRequest){
        CommunitySaveResponse communitySaveResponse = communityService.createCommunity(authUser,communitySaveRequest);
        return ResponseEntity.ok(communitySaveResponse);
    }

}
