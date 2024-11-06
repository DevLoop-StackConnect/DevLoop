package com.devloop.lecturereview.controller;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.ApiResponse;
import com.devloop.lecturereview.request.SaveLectureReviewRequest;
import com.devloop.lecturereview.response.GetLectureReviewResponse;
import com.devloop.lecturereview.service.LectureReviewService;
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
public class LectureReviewController {
    private final LectureReviewService lectureReviewService;

    //강의 후기 등록 (수강 유저만 가능)
    @PostMapping("/v2/lectures/{lectureId}/reviews")
    public ApiResponse<String> saveLectureReview(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable("lectureId") Long lectureId,
            @Valid @RequestBody SaveLectureReviewRequest saveLectureReviewRequest
    ) {
        return ApiResponse.ok(lectureReviewService.saveLectureReview(authUser, lectureId, saveLectureReviewRequest));
    }

    //강의 후기 수정 (수강 유저만 가능)
    @PatchMapping("/v2/lectures/{lectureId}/reviews/{reviewId}")
    public ApiResponse<String> updateLectureReview(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable("lectureId") Long lectureId,
            @PathVariable("reviewId") Long reviewId,
            @Valid @RequestBody SaveLectureReviewRequest lectureReviewRequest
    ) {
        return ApiResponse.ok(lectureReviewService.updateLectureReview(authUser, lectureId, reviewId, lectureReviewRequest));
    }

    //강의 후기 다건 조회
    @GetMapping("/v2/search/lectures/{lectureId}/reviews")
    @PreAuthorize("permitAll()")
    public ApiResponse<Page<GetLectureReviewResponse>> getLectureReviewList(
            @PathVariable("lectureId") Long lectureId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.ok(lectureReviewService.getLectureReviewList(lectureId, page, size));
    }

    //강의 후기 삭제
    @DeleteMapping("/v2/lectures/{lectureId}/reviews/{reviewId}")
    @PreAuthorize("#authUser.id == authentication.principal.id or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteLectureReview(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable("lectureId") Long lectureId,
            @PathVariable("reviewId") Long reviewId
    ) {
        lectureReviewService.deleteLectureReview(authUser, lectureId, reviewId);
        return ResponseEntity.noContent().build();
    }
}
