package com.devloop.lecture.controller;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.ApiResponse;
import com.devloop.lecture.service.LectureVideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LectureVideoController {
    private final LectureVideoService lectureVideoService;

    /**
     * 강의 영상파일 등록
     * @param lectureId
     * @param multipartFile
     * @return
     * @throws IOException
     */
    @PostMapping("/v2/lectures/{lectureId}/videos/multipart-upload")
    public ApiResponse<String> uploadVideo(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable("lectureId") Long lectureId,
            @RequestParam(value = "file", required = false) MultipartFile multipartFile
    ) throws IOException {
        return ApiResponse.ok(lectureVideoService.uploadVideo(authUser,lectureId,multipartFile));
    }


}
