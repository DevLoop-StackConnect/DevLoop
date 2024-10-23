package com.devloop.pwt.controller;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.ApiResponse;
import com.devloop.pwt.entity.ProjectWithTutor;
import com.devloop.pwt.request.ProjectWithTutorSaveRequest;
import com.devloop.pwt.request.ProjectWithTutorUpdateRequest;
import com.devloop.pwt.service.ProjectWithTutorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProjectWithTutorController {

    private final ProjectWithTutorService projectWithTutorService;

    // todo : CRUD API 완성후 WebSecurityConfig에 접근 권한 ADMIN, TUTOR로 설정하기

    // 튜터랑 함께하는 협업 프로젝트 게시글 생성
    @PostMapping("/v1/pwts")
    public ApiResponse<String> saveProjectWithTutor(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam("file") MultipartFile file,
            @Valid @ModelAttribute ProjectWithTutorSaveRequest projectWithTutorSaveRequest
    ) {
        return ApiResponse.ok(projectWithTutorService.saveProjectWithTutor(authUser, file, projectWithTutorSaveRequest));
    }

    // 튜터랑 함께하는 협업 프로젝트 게시글 수정
    @PatchMapping("/v1/pwts/{pwtId}")
    public ApiResponse<String> updateProjectWithTutor(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable("pwtId") Long projectId,
            @RequestParam("file") MultipartFile file,
            @Valid @ModelAttribute ProjectWithTutorUpdateRequest projectWithTutorUpdateRequest

    ){
        return ApiResponse.ok(projectWithTutorService.updateProjectWithTutor(authUser, projectId, file, projectWithTutorUpdateRequest));
    }

    // 튜터랑 함께하는 협업 프로젝트 게시글 삭제
    @DeleteMapping("/v1/pwts/{pwtId}")
    public ApiResponse<String> deleteProjectWithTutor(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable("pwtId") Long projectId
    ){
        return ApiResponse.ok(projectWithTutorService.deleteProjectWithTutor(authUser, projectId));
    }
}
