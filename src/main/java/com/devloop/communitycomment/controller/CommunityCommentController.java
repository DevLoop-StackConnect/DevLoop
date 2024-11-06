package com.devloop.communitycomment.controller;

import jakarta.validation.Valid;
import com.devloop.common.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.devloop.common.apipayload.ApiResponse;
import com.devloop.communitycomment.response.CommentResponse;
import com.devloop.communitycomment.request.CommentSaveRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import com.devloop.communitycomment.request.CommentUpdateRequest;
import com.devloop.communitycomment.response.CommentSaveResponse;
import com.devloop.communitycomment.response.CommentUpdateResponse;
import com.devloop.communitycomment.service.CommunityCommentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommunityCommentController {
    private final CommunityCommentService communityCommentService;

    //댓글 작성
    @PostMapping("/v1/communities/{communityId}/comments")
    public ApiResponse<CommentSaveResponse> createComment(@AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody CommentSaveRequest commentSaveRequest, @PathVariable Long communityId) {
        return ApiResponse.ok(communityCommentService.createComment(authUser, commentSaveRequest, communityId));
    }

    //댓글 수정
    @PatchMapping("/v1/communities/{communityId}/comments/{commentId}")
    public ApiResponse<CommentUpdateResponse> updateComment(@AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody CommentUpdateRequest commentUpdateRequest, @PathVariable Long communityId, @PathVariable Long commentId) {
        return ApiResponse.ok(communityCommentService.updateComment(authUser, commentUpdateRequest, communityId, commentId));
    }

    //댓글 삭제
    @DeleteMapping("/v1/communities/{communityId}/comments/{commentId}")
    @PreAuthorize("#authUser.id == authentication.principal.id or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteComment(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long communityId, @PathVariable Long commentId) {
        communityCommentService.deleteComment(authUser, communityId, commentId);
        return ResponseEntity.noContent().build();
    }

    //댓글 다건 조회
    @GetMapping("/v1/communities/{communityId}/comments")
    @PreAuthorize("permitAll()")
    public ApiResponse<Page<CommentResponse>> getComments(@PathVariable Long communityId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(communityCommentService.getComments(communityId, page, size));
    }
}
