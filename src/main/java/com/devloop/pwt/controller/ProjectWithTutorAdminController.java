package com.devloop.pwt.controller;

import com.devloop.common.apipayload.ApiResponse;
import com.devloop.pwt.service.ProjectWithTutorAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
