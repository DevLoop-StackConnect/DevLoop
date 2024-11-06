package com.devloop.lecture.controller;

import com.devloop.common.apipayload.ApiResponse;
import com.devloop.lecture.response.GetLectureDetailResponse;
import com.devloop.lecture.response.GetLectureListResponse;
import com.devloop.lecture.service.LectureAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LectureAdminController {

    private final LectureAdminService lectureAdminService;

    //강의 승인 (ADMIN)
    @PatchMapping("/v2/admin/lectures/{lectureId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<String> changeApproval(
            @PathVariable("lectureId") Long lectureId
    ) {
        return ApiResponse.ok(lectureAdminService.changeApproval(lectureId));
    }
    //강의 단건 조회 (ADMIN : User 정보 포함 단건 조회)
    @GetMapping("/v2/admin/lectures/{lectureId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<GetLectureDetailResponse> getLecture(
            @PathVariable("lectureId") Long lectureId
    ) {
        return ApiResponse.ok(lectureAdminService.getLecture(lectureId));
    }
    //승인 되지 않은 강의 다건 조회 (ADMIN)
    @GetMapping("/v2/admin/lectures")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<Page<GetLectureListResponse>> getLectureList(
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.ok(lectureAdminService.getLectureList(title, page, size));
    }
}
