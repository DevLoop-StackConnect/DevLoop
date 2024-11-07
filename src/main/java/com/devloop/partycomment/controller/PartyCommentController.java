package com.devloop.partycomment.controller;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.ApiResponse;
import com.devloop.partycomment.request.SavePartyCommentRequest;
import com.devloop.partycomment.request.UpdatePartyCommentRequest;
import com.devloop.partycomment.response.GetPartyCommentListResponse;
import com.devloop.partycomment.response.SavePartyCommentResponse;
import com.devloop.partycomment.response.UpdatePartyCommentResponse;
import com.devloop.partycomment.service.PartyCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PartyCommentController {
    private final PartyCommentService partyCommentService;

    //스터디 파티 게시글 댓글 등록
    @PostMapping("/v1/parties/{partyId}/comments")
    public ApiResponse<SavePartyCommentResponse> savePartyComment(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable("partyId") Long partyId,
            @Valid @RequestBody SavePartyCommentRequest savePartyCommentRequest
    ) {
        return ApiResponse.ok(partyCommentService.savePartyComment(authUser, partyId, savePartyCommentRequest));
    }

    //스터디 파디 게시글 댓글 수정
    @PatchMapping("/v1/parties/{partyId}/comments/{commentId}")
    public ApiResponse<UpdatePartyCommentResponse> updatePartyComment(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable("partyId") Long partyId,
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody UpdatePartyCommentRequest updatePartyCommentRequest
    ) {
        return ApiResponse.ok(partyCommentService.updatePartyComment(authUser, partyId, commentId, updatePartyCommentRequest));
    }

    //스터디 파티 게시글 댓글 다건 조회
    @GetMapping("/search/v1/parties/{partyId}/comments")
    public ApiResponse<Page<GetPartyCommentListResponse>> getPartyCommentList(
            @PathVariable("partyId") Long partyId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.ok(partyCommentService.getPartyCommentList(partyId, page, size));
    }

    //스터디 파티 게시글 댓글 삭제
    @DeleteMapping("/v1/parties/{partyId}/comments/{commentId}")
    public ResponseEntity<Void> deletePartyComment(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable("partyId") Long partyId,
            @PathVariable("commentId") Long commentId
    ) {
        partyCommentService.deletePartyComment(authUser, partyId, commentId);
        return ResponseEntity.noContent().build();
    }
}
