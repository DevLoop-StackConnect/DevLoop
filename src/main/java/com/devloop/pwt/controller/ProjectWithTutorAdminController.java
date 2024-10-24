package com.devloop.pwt.controller;

import com.devloop.common.apipayload.ApiResponse;
import com.devloop.pwt.response.ProjectWithTutorDetailAdminResponse;
import com.devloop.pwt.service.ProjectWithTutorAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProjectWithTutorAdminController {

    private final ProjectWithTutorAdminService projectWithTutorAdminService;

    // PWT 게시글 승인 (ADMIN)
    @PatchMapping("/v1/admin/pwts/{pwtId}")
    public ApiResponse<String> changeApproval(
            @PathVariable("pwtId") Long pwtId
    ){
        return ApiResponse.ok(projectWithTutorAdminService.changeApproval(pwtId));
    }

    // 튜터랑 함께하는 협업 프로젝트 게시글 단건 조회(ADMIN : User 정보 포함 단건 조회)
    @GetMapping("/v1/admin/pwts/{pwtId}")
    public ApiResponse<ProjectWithTutorDetailAdminResponse> getProjectWithTutor(
            @PathVariable("pwtId") Long projectId
    ){
        return ApiResponse.ok(projectWithTutorAdminService.getProjectWithTutor(projectId));
    }
}
