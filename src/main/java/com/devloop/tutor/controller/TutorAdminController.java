package com.devloop.tutor.controller;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.ApiResponse;
import com.devloop.tutor.response.TutorRequestResponse;
import com.devloop.tutor.service.TutorAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TutorAdminController {

    private final TutorAdminService tutorAdminService;

    // 튜터 신청 요청 조회 (ADMIN : 승인되지 않은 튜터 신청 요청 다건 조회)
    @GetMapping("/v1/admin/tutor-request")
    public ApiResponse<Page<TutorRequestResponse>> getAllTutorRequest(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.ok(tutorAdminService.getAllTutorRequest(page, size));
    }


}
