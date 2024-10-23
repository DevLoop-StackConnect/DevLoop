package com.devloop.communitycomment.controller;

import com.devloop.common.AuthUser;
import com.devloop.communitycomment.dto.CommentResponse;
import com.devloop.communitycomment.dto.request.CommentSaveRequest;
import com.devloop.communitycomment.dto.request.CommentUpdateRequest;
import com.devloop.communitycomment.dto.response.CommentSaveResponse;
import com.devloop.communitycomment.dto.response.CommentUpdateResponse;
import com.devloop.communitycomment.service.CommunityCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/communities/{communityId}/comments")
public class CommunityCommentController {
    private final CommunityCommentService communityCommentService;

    //댓글 작성
    @PostMapping
    public ResponseEntity<CommentSaveResponse> createComment(@AuthenticationPrincipal AuthUser authUser, @RequestBody CommentSaveRequest commentSaveRequest, @PathVariable Long communityId){
       CommentSaveResponse commentSaveResponse = communityCommentService.creatComment(authUser,commentSaveRequest,communityId);
       return ResponseEntity.ok(commentSaveResponse);
    }

    //댓글 수정
    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentUpdateResponse> updateComment(@AuthenticationPrincipal AuthUser authUser, @RequestBody CommentUpdateRequest commentUpdateRequest, @PathVariable Long communityId, @PathVariable Long commentId){
        CommentUpdateResponse commentUpdateResponse = communityCommentService.updateComment(authUser,commentUpdateRequest,communityId,commentId);
        return  ResponseEntity.ok(commentUpdateResponse);
    }

    //댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long communityId, @PathVariable Long commentId){
        communityCommentService.deleteComment(authUser,communityId,commentId);
        return ResponseEntity.ok().build();
    }

    //댓글 조회
    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long communityId ){
        List<CommentResponse> comments = communityCommentService.getComments(communityId);
        return ResponseEntity.ok(comments);
    }
}
