package com.devloop.lecturereview.controller;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.ApiResponse;
import com.devloop.lecturereview.request.LectureReviewRequest;
import com.devloop.lecturereview.response.LectureReviewResponse;
import com.devloop.lecturereview.service.LectureReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LectureReviewController {
    private final LectureReviewService lectureReviewService;

    //강의 후기 등록
    @PostMapping("/v2/lectures/{lectureId}/reviews")
    public ApiResponse<String> saveLectureReview(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable("lectureId") Long lectureId,
            @Valid @RequestBody LectureReviewRequest saveLectureReviewRequest
    ){
        return ApiResponse.ok(lectureReviewService.saveLectureReview(authUser,lectureId,saveLectureReviewRequest));
    }

    //강의 후기 수정
    @PatchMapping("/v2/lectures/{lectureId}/reviews/{reviewId}")
    public ApiResponse<String> updateLectureReview(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable("lectureId") Long lectureId,
            @PathVariable("reviewId") Long reviewId,
            @Valid @RequestBody LectureReviewRequest lectureReviewRequest
    ){
        return ApiResponse.ok(lectureReviewService.updateLectureReview(authUser,lectureId,reviewId,lectureReviewRequest));
    }

    //강의 후기 다건 조회
    @GetMapping("/v2/search/lectures/{lectureId}/reviews")
    public ApiResponse<Page<LectureReviewResponse>> getLectureReviewList(
            @PathVariable("lectureId") Long lectureId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ApiResponse.ok(lectureReviewService.getLectureReviewList(lectureId,page,size));
    }

}
