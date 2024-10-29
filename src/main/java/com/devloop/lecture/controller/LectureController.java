package com.devloop.lecture.controller;


import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.ApiResponse;
import com.devloop.lecture.request.SaveLectureRequest;
import com.devloop.lecture.service.LectureService;
import lombok.RequiredArgsConstructor;
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


}
