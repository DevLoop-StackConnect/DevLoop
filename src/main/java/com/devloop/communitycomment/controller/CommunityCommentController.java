package com.devloop.communitycomment.controller;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.ApiResponse;
import com.devloop.communitycomment.dto.CommentResponse;
import com.devloop.communitycomment.dto.request.CommentSaveRequest;
import com.devloop.communitycomment.dto.request.CommentUpdateRequest;
import com.devloop.communitycomment.dto.response.CommentSaveResponse;
import com.devloop.communitycomment.dto.response.CommentUpdateResponse;
import com.devloop.communitycomment.service.CommunityCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommunityCommentController {
    private final CommunityCommentService communityCommentService;

    //댓글 작성
    @PostMapping("/v1/communities/{communityId}/comments")
    public ApiResponse<CommentSaveResponse> createComment(@AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody CommentSaveRequest commentSaveRequest, @PathVariable Long communityId){
       CommentSaveResponse commentSaveResponse = communityCommentService.creatComment(authUser,commentSaveRequest,communityId);
       return ApiResponse.ok(commentSaveResponse);
    }

    //댓글 수정
    @PatchMapping("/v1/communities/{communityId}/comments/{commentId}")
    public ApiResponse<CommentUpdateResponse> updateComment(@AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody CommentUpdateRequest commentUpdateRequest, @PathVariable Long communityId, @PathVariable Long commentId){
        CommentUpdateResponse commentUpdateResponse = communityCommentService.updateComment(authUser,commentUpdateRequest,communityId,commentId);
        return  ApiResponse.ok(commentUpdateResponse);
    }

    //댓글 삭제
    @DeleteMapping("/v1/communities/{communityId}/comments/{commentId}")
    public ApiResponse<Void> deleteComment(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long communityId, @PathVariable Long commentId){
        communityCommentService.deleteComment(authUser,communityId,commentId);
        return ApiResponse.ok(null);
    }

    //댓글 조회
    @GetMapping
    public ApiResponse<List<CommentResponse>> getComments(@PathVariable Long communityId ){
        List<CommentResponse> comments = communityCommentService.getComments(communityId);
        return ApiResponse.ok(comments);
    }
}
