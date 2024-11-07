package com.devloop.tutor.controller;

import com.devloop.common.apipayload.ApiResponse;
import com.devloop.tutor.response.TutorRequestListAdminResponse;
import com.devloop.tutor.service.TutorAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TutorAdminController {

    private final TutorAdminService tutorAdminService;

    // 튜터 신청 요청 조회 (ADMIN : 승인되지 않은 튜터 신청 요청 다건 조회)
    @PreAuthorize("hasRole('ROLE_ADMIN')")  // ROLE_ADMIN인 경우 접근 가능
    @GetMapping("/v1/admin/tutor-request")
    public ApiResponse<Page<TutorRequestListAdminResponse>> getAllTutorRequest(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.ok(tutorAdminService.getAllTutorRequest(page, size));
    }

    // 튜터 신청 승인 (ADMIN : 튜터로 사용자 권한 변경)
    @PreAuthorize("hasRole('ROLE_ADMIN')")  // ROLE_ADMIN인 경우 접근 가능
    @PatchMapping("/v1/admin/users/{userId}")
    public ApiResponse<String> changeUserRoleToTutor(
            @PathVariable("userId") Long userId
    ) {
        return ApiResponse.ok(tutorAdminService.changeUserRoleToTutor(userId));
    }

}
