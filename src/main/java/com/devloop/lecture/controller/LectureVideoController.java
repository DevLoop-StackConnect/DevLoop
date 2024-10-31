package com.devloop.lecture.controller;
import com.devloop.common.apipayload.ApiResponse;
import com.devloop.lecture.request.MultipartUploadCompleteRequest;
import com.devloop.lecture.service.LectureVideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LectureVideoController {
    private final LectureVideoService lectureVideoService;

    /**
     * 멀티파트 업로드 시작 요청 메소드
     * S3로 부터 고유 식별자인 Upload Id 발급
     * @param fileName //S3에 파일을 저장할 때, 해당 파일을 식별하는 고유 키 역할
     * @return Upload Id
     */
    @PostMapping("/v2/videos/initiate-multipart-upload")
    public ApiResponse<String> getUploadId(
            @RequestParam String fileName
    ){
        return ApiResponse.ok(lectureVideoService.getUploadId(fileName));
    }

    /**
     * Upload Id를 기반으로 객체 업로드를 위한 Authorization 정보 발급
     * @param uploadId //발급 받은 uploadId
     * @param fileName
     * @param partCount //파일을 나눈 파트의 총 개수, 몇개의 파트로 나누어 업로드 할 지
     * @return part별 PreSignedURL
     */
    @PostMapping("/v2/videos/upload-part-url")
    public ApiResponse<Map<Integer,String>> getPresignedUrlsForParts(
            @RequestParam String uploadId,
            @RequestParam String fileName,
            @RequestParam int partCount
    ){
        return ApiResponse.ok(lectureVideoService.getPresignedUrlsForParts(uploadId,fileName,partCount));
    }

    /**
     * ETag를 기준으로 S3서버에 Part별 Upload Complete 요청
     * 모든 part가 업로드 된 후, 조각들을 병합하여 최종 파일로 만든다
     * @param lectureId
     * @param uploadCompleteRequest
     * @return
     */
    @PostMapping("/v2/lectures/{lectureId}/videos/complete-multipart-upload")
    public ApiResponse<String> completeMultipartUpload(
            @PathVariable("lectureId") Long lectureId,
            @RequestBody MultipartUploadCompleteRequest uploadCompleteRequest
    ) throws MalformedURLException {
        return ApiResponse.ok(lectureVideoService.completeMultipartUpload(lectureId,uploadCompleteRequest));
    }

}
