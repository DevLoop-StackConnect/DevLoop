package com.devloop.lecturereview.controller;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.ApiResponse;
import com.devloop.lecture.request.SaveLectureRequest;
import com.devloop.lecturereview.request.SaveLectureReviewRequest;
import com.devloop.lecturereview.service.LectureReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
            @Valid @RequestBody SaveLectureReviewRequest saveLectureReviewRequest
    ){
        return ApiResponse.ok(lectureReviewService.saveLectureReview(authUser,lectureId,saveLectureReviewRequest));
    }
}
