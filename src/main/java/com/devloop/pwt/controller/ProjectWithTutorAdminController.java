package com.devloop.pwt.controller;

import com.devloop.common.apipayload.ApiResponse;
import com.devloop.pwt.response.ProjectWithTutorDetailAdminResponse;
import com.devloop.pwt.response.ProjectWithTutorListAdminResponse;
import com.devloop.pwt.service.ProjectWithTutorAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProjectWithTutorAdminController {

    private final ProjectWithTutorAdminService projectWithTutorAdminService;

    // PWT 게시글 승인 (ADMIN)
    @PreAuthorize("hasRole('ROLE_ADMIN')")  // ROLE_ADMIN 권한만 접근 가능
    @PatchMapping("/v1/admin/pwts/{pwtId}")
    public ApiResponse<String> changeApproval(
            @PathVariable("pwtId") Long pwtId
    ) {
        return ApiResponse.ok(projectWithTutorAdminService.changeApproval(pwtId));
    }

    // 튜터랑 함께하는 협업 프로젝트 게시글 단건 조회(ADMIN : User 정보 포함 단건 조회)
    @PreAuthorize("hasRole('ROLE_ADMIN')")  // ROLE_ADMIN 권한만 접근 가능
    @GetMapping("/v1/admin/pwts/{pwtId}")
    public ApiResponse<ProjectWithTutorDetailAdminResponse> getProjectWithTutor(
            @PathVariable("pwtId") Long projectId
    ) {
        return ApiResponse.ok(projectWithTutorAdminService.getProjectWithTutor(projectId));
    }

    // 튜터랑 함께하는 협업 프로젝트 승인되지 않은 게시글 다건 조회 (ADMIN : TEXT 타입 데이터 제외 다건 조회)
    @PreAuthorize("hasRole('ROLE_ADMIN')")  // ROLE_ADMIN 권한만 접근 가능
    @GetMapping("/v1/admin/pwts")
    public ApiResponse<Page<ProjectWithTutorListAdminResponse>> getAllProjectWithTutors(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.ok(projectWithTutorAdminService.getAllProjectWithTutors(page, size));
    }
}
