package com.devloop.pwt.controller;

import com.devloop.attachment.service.PWTAttachmentService;
import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.ApiResponse;
import com.devloop.pwt.entity.ProjectWithTutor;
import com.devloop.pwt.request.ProjectWithTutorSaveRequest;
import com.devloop.pwt.request.ProjectWithTutorUpdateRequest;
import com.devloop.pwt.response.ProjectWithTutorDetailResponse;
import com.devloop.pwt.response.ProjectWithTutorListResponse;
import com.devloop.pwt.service.ProjectWithTutorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProjectWithTutorController {

    private final ProjectWithTutorService projectWithTutorService;

    // 튜터랑 함께하는 협업 프로젝트 게시글 생성 (일반 사용자 접근 불가)
    @PostMapping("/v1/tutor/pwts")
    public ApiResponse<String> saveProjectWithTutor(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam("file") MultipartFile file,
            @Valid @ModelAttribute ProjectWithTutorSaveRequest projectWithTutorSaveRequest
    ) {
        return ApiResponse.ok(projectWithTutorService.saveProjectWithTutor(authUser, file, projectWithTutorSaveRequest));
    }

    // 튜터랑 함께하는 협업 프로젝트 게시글 단건 조회 (승인이 완료된 게시글 단건 조회)
    @GetMapping("/v1/pwts/{pwtId}")
    public ApiResponse<ProjectWithTutorDetailResponse> getProjectWithTutor(
            @PathVariable("pwtId") Long projectId
    ){
        return ApiResponse.ok(projectWithTutorService.getProjectWithTutor(projectId));
    }

    // 튜터랑 함께하는 협업 프로젝트 게시글 다건 조회 (승인이 완료된 게시글 다건 조회)
    @GetMapping("/v1/pwts")
    public ApiResponse<Page<ProjectWithTutorListResponse>> getAllProjectWithTutors(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ApiResponse.ok(projectWithTutorService.getAllProjectWithTutors(page,size));
    }

    // 튜터랑 함께하는 협업 프로젝트 게시글 수정 (일반 사용자 접근 불가)
    @PatchMapping("/v1/tutor/pwts/{pwtId}")
    public ApiResponse<String> updateProjectWithTutor(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable("pwtId") Long projectId,
            @RequestParam("file") MultipartFile file,
            @Valid @ModelAttribute ProjectWithTutorUpdateRequest projectWithTutorUpdateRequest

    ){
        return ApiResponse.ok(projectWithTutorService.updateProjectWithTutor(authUser, projectId, file, projectWithTutorUpdateRequest));
    }

    // 튜터랑 함께하는 협업 프로젝트 게시글 삭제 (일반 사용자 접근 불가)
    @DeleteMapping("/v1/tutor/pwts/{pwtId}")
    public ApiResponse<String> deleteProjectWithTutor(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable("pwtId") Long projectId
    ){
        return ApiResponse.ok(projectWithTutorService.deleteProjectWithTutor(authUser, projectId));
    }

}
