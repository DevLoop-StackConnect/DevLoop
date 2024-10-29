package com.devloop.lecture.controller;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.ApiResponse;
import com.devloop.lecture.request.SaveLectureRequest;
import com.devloop.lecture.request.UpdateLectureRequest;
import com.devloop.lecture.response.LectureDetailResponse;
import com.devloop.lecture.response.LectureListResponse;
import com.devloop.lecture.service.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LectureController {
    private final LectureService lectureService;

    //강의 생성 (일반 사용자 접근 불가)
    @PostMapping("/v2/lectures")
    public ApiResponse<String> saveLecture(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody SaveLectureRequest saveLectureRequest
    ){
        return ApiResponse.ok(lectureService.saveLecture(authUser,saveLectureRequest));
    }

    //강의 수정 (일반 사용자 접근 불가)
    @PatchMapping("/v2/lectures/{lectureId}")
    public ApiResponse<String> updateLecture(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable("lectureId") Long lectureId,
            @RequestBody UpdateLectureRequest updateLectureRequest
    ){
        return ApiResponse.ok(lectureService.updateLecture(authUser,lectureId,updateLectureRequest));
    }

    //강의 단건 조회 (승인이 완료된 강의)
    @GetMapping("/v2/search/lectures/{lectureId}")
    public ApiResponse<LectureDetailResponse> getLecture(
            @PathVariable("lectureId") Long lectureId
    ){
        return ApiResponse.ok(lectureService.getLecture(lectureId));
    }

    //강의 다건 조회
    @GetMapping("/v2/search/lectures")
    public ApiResponse<Page<LectureListResponse>> getLectureList(
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ApiResponse.ok(lectureService.getLectureList(title,page,size));
    }


    //강의 삭제
    @DeleteMapping("/v2/lectures/{lectureId}")
    public ApiResponse<String> deleteLecture(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable("lectureId") Long lectureId
    ){
        return ApiResponse.ok(lectureService.deleteLecture(authUser,lectureId));
    }

}
