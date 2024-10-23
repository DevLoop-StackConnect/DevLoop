package com.devloop.tutor.controller;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.ApiResponse;
import com.devloop.tutor.entity.TutorRequest;
import com.devloop.tutor.request.TutorRequestSaveRequest;
import com.devloop.tutor.service.TutorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TutorController {

    private final TutorService tutorService;

    // 튜터 신청
    @PostMapping("/v1/users/tutor-request")
    public ApiResponse<String> saveTutorRequest(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody TutorRequestSaveRequest tutorRequest
    ) {
        return ApiResponse.ok(tutorService.saveTutorRequest(authUser, tutorRequest));
    }

}
