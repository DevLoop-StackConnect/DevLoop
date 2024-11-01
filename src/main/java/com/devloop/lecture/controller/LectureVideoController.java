package com.devloop.lecture.controller;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.ApiResponse;
import com.devloop.lecture.response.GetLectureVideoDetailResponse;
import com.devloop.lecture.response.GetLectureVideoListResponse;
import com.devloop.lecture.service.LectureVideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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
    public ApiResponse<String> uploadLectureVideo(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable("lectureId") Long lectureId,
            @RequestParam(value = "file") MultipartFile multipartFile,
            @RequestParam(value = "title") String title
    ) throws IOException {
        return ApiResponse.ok(lectureVideoService.uploadLectureVideo(authUser,lectureId,multipartFile, title));
    }

    /**
     * 영상 다건 조회 (승인이 완료된 강의만 조회)
     * 수강 유저 -> 영상 url
     * 일반 유저 -> 영상 제목
     * @param authUser
     * @param lectureId
     * @return
     */
    @GetMapping("/v2/lectures/{lectureId}/videos")
    public ApiResponse<List<GetLectureVideoListResponse>> getLectureVideoList(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable("lectureId") Long lectureId
    ){
        return ApiResponse.ok(lectureVideoService.getLectureVideoList(authUser,lectureId));
    }

    /**
     * 강의 단건 조회 (수강 유저와 어드민만 접근)
     * @param authUser
     * @param videoId
     * @return
     */
    @GetMapping("/v2/videos/{videoId}")
    public ApiResponse<GetLectureVideoDetailResponse> getLectureVideo(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable("videoId") Long videoId
    ) throws Exception {
        return ApiResponse.ok(lectureVideoService.getLectureVideo(authUser,videoId));
    }

    /**
     * 강의 영상 삭제
     * @param authUser
     * @param videoId
     * @return
     */
    @DeleteMapping("/v2/videos/{videoId}")
    public ApiResponse<String> deleteVideo(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable("videoId") Long videoId
    ){
        return ApiResponse.ok(lectureVideoService.deleteVideo(authUser,videoId));
    }


}
