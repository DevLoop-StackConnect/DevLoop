package com.devloop.lecture.controller;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.ApiResponse;
import com.devloop.lecture.response.GetLectureVideoDetailResponse;
import com.devloop.lecture.response.GetLectureVideoListResponse;
import com.devloop.lecture.service.LectureVideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LectureVideoController {
    private final LectureVideoService lectureVideoService;

    //강의 영상파일 등록
    @PostMapping("/v2/lectures/{lectureId}/videos/multipart-upload")
    @PreAuthorize("hasRole('ROLE_TUTOR')")
    public ApiResponse<String> uploadLectureVideo(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable("lectureId") Long lectureId,
            @RequestParam(value = "file") MultipartFile multipartFile,
            @RequestParam(value = "title") String title
    ) {
        return ApiResponse.ok(lectureVideoService.uploadLectureVideo(authUser, lectureId, multipartFile, title));
    }

    //영상 다건 조회 (승인이 완료된 강의만 조회)
    @GetMapping("/v2/lectures/{lectureId}/videos")
    @PreAuthorize("permitAll()")
    public ApiResponse<List<GetLectureVideoListResponse>> getLectureVideoList(
            @PathVariable("lectureId") Long lectureId
    ) {
        return ApiResponse.ok(lectureVideoService.getLectureVideoList(lectureId));
    }

    //강의 단건 조회 (수강 유저와 어드민만 접근)
    @GetMapping("/v2/lectures/{lectureId}/videos/{videoId}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<GetLectureVideoDetailResponse> getLectureVideo(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable("lectureId") Long lectureId,
            @PathVariable("videoId") Long videoId
    ) {
        return ApiResponse.ok(lectureVideoService.getLectureVideo(authUser, lectureId, videoId));
    }

    //강의 영상 삭제
    @DeleteMapping("/v2/lectures/{lectureId}/videos/{videoId}")
    @PreAuthorize("hasRole('ROLE_TUTOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteVideo(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable("lectureId") Long lectureId,
            @PathVariable("videoId") Long videoId
    ) {
        lectureVideoService.deleteVideo(authUser, lectureId, videoId);
        return ResponseEntity.noContent().build();
    }
}
