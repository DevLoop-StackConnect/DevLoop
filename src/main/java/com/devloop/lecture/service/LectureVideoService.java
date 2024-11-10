package com.devloop.lecture.service;

import com.devloop.attachment.cloudfront.CloudFrontService;
import com.devloop.attachment.enums.FileFormat;
import com.devloop.attachment.s3.S3Service;
import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.enums.Approval;
import com.devloop.common.exception.ApiException;
import com.devloop.common.validator.FileValidator;
import com.devloop.lecture.entity.Lecture;
import com.devloop.lecture.entity.LectureVideo;
import com.devloop.lecture.enums.VideoStatus;
import com.devloop.lecture.repository.LectureVideoRepository;
import com.devloop.lecture.response.GetLectureVideoDetailResponse;
import com.devloop.lecture.response.GetLectureVideoListResponse;
import com.devloop.purchase.service.PurchaseService;
import com.devloop.user.entity.User;
import com.devloop.user.enums.UserRole;
import com.devloop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LectureVideoService {
    private final S3Client s3Client;
    private final LectureVideoRepository lectureVideoRepository;
    private final UserService userService;
    private final S3Service s3Service;
    private final CloudFrontService cloudFrontService;
    private final LectureService lectureService;
    private final FileValidator fileValidator;
    private final PurchaseService purchaseService;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    /**
     * 멀티파트 파일 업로드
     *
     * @param authUser
     * @param lectureId
     * @param multipartFile
     * @param title
     * @return
     * @throws IOException
     */
    public String uploadLectureVideo(AuthUser authUser, Long lectureId, MultipartFile multipartFile, String title) {
        //유저가 존재하는 지 확인
        User user = userService.findByUserId(authUser.getId());

        //강의가 존재하는 지 확인
        Lecture lecture = lectureService.findById(lectureId);

        //유저가 강의 등록한 유저인지 확인
        if (!user.getId().equals(lecture.getUser().getId())) {
            throw new ApiException(ErrorStatus._HAS_NOT_ACCESS_PERMISSION);
        }

        //파일 타입 확인(사진으로 테스트하기 위해 임시로 주석 처리)
        fileValidator.fileTypeValidator(multipartFile,lecture);
        FileFormat fileType = fileValidator.mapStringToFileFormat(Objects.requireNonNull(multipartFile.getContentType()));

        //파일 사이즈 확인 (5GB까지 가능)
        fileValidator.fileSizeValidator(multipartFile, 5L * 1024 * 1024 * 1024);
        long fileSize = multipartFile.getSize();

        String folderPath = "lectures/" + lectureId + "/";
        String fileName = makeFileName(folderPath, multipartFile);
        long partSize = 5 * 1024 * 1024; //5MB 단위로 파트 분할
        String uploadId = null;
        List<CompletedPart> completedParts = new ArrayList<>();

        //멀티파트 업로드 시작 요청 및 UploadId 반환
        uploadId = getUploadId(fileName);

        //파일을 partSize만큼 파트로 나누어 업로드
        long filePosition = 0;

        try (InputStream inputStream = multipartFile.getInputStream()) {
            for (int i = 1; filePosition < fileSize; i++) {
                //마지막 파트 크기가 partSize 미만일 경우 조정
                long currentPartSize = Math.min(partSize, (fileSize - filePosition));

                //각 파트에 대한 객체 생성
                UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .uploadId(uploadId)
                        .partNumber(i)
                        .build();

                //각 파트를 업로드하고 ETag를 partETags에 추가
                UploadPartResponse uploadPartResponse = s3Client.uploadPart(
                        uploadPartRequest,
                        RequestBody.fromInputStream(inputStream, currentPartSize)
                );

                CompletedPart part = CompletedPart.builder()
                        .partNumber(i)
                        .eTag(uploadPartResponse.eTag())
                        .build();

                completedParts.add(part);

                filePosition += currentPartSize;
            }

            //멀티 파트 업로드 완료 요청
            CompleteMultipartUploadRequest completeMultipartUploadRequest = CompleteMultipartUploadRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .uploadId(uploadId)
                    .multipartUpload(CompletedMultipartUpload.builder().parts(completedParts).build())
                    .build();

            s3Client.completeMultipartUpload(completeMultipartUploadRequest);

            //새로운 강의 영상 객체 생성
            LectureVideo lectureVideo = LectureVideo.of(
                    fileName,
                    title,
                    VideoStatus.COMPLETED,
                    fileType,
                    lecture
            );
            lectureVideoRepository.save(lectureVideo);

            return fileName;

        } catch (S3Exception e) {
            //예외 발생 시, 업로드 취소
            s3Client.abortMultipartUpload(AbortMultipartUploadRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .uploadId(uploadId)
                    .build());

            throw new ApiException(ErrorStatus._S3_UPLOAD_ERROR);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    //파일 이름 생성
    public String makeFileName(String folderPath, MultipartFile multipartFile) {
        return folderPath + UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
    }

    //Upload Id 반환
    public String getUploadId(String fileName) {
        //어떤 bucket에 어떤 object를 업로드할 것인지에 대한 Request 정보 생성하기 위해 MultipartUploadRequest 빌드하여 객체 생성
        CreateMultipartUploadRequest request = CreateMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        //생성된 객체를 createMultipartUpload 메서드에 전달
        CreateMultipartUploadResponse response = s3Client.createMultipartUpload(request);

        return response.uploadId();
    }

    //강의 영상 다건 조회
    public List<GetLectureVideoListResponse> getLectureVideoList(Long lectureId) {
        //강의가 존재하는 지 확인
        Lecture lecture = lectureService.findById(lectureId);

        //강의가 승인이 되었는 지 확인
        if (!lecture.getApproval().equals(Approval.APPROVED)) {
            throw new ApiException(ErrorStatus._ACCESS_PERMISSION_DENIED);
        }

        List<LectureVideo> lectureVideos = lectureVideoRepository.findAllByLectureId(lectureId);

        return lectureVideos.stream().map(lectureVideo -> {
            return GetLectureVideoListResponse.of(
                    lectureVideo.getId(),
                    lectureVideo.getTitle()
            );
        }).toList();
    }

    /**
     * 강의 영상 단건 조회 (수강 유저와 어드민만 조회 가능)
     *
     * @param authUser
     * @param lectureId
     * @param videoId
     * @return
     */
    public GetLectureVideoDetailResponse getLectureVideo(AuthUser authUser, Long lectureId, Long videoId) {
        //유저가 존재하는 지 확인
        User user = userService.findByUserId(authUser.getId());

        //수강 여부 확인
        boolean isPurchased = purchaseService.exitsByUserIdAndProductId(authUser.getId(), lectureId);
        boolean isAdminUser = user.getUserRole().equals(UserRole.ROLE_ADMIN);

        //수강 유저 또는 어드민이 아닌 경우 권한 없음
        if (!isPurchased && !isAdminUser) {
            throw new ApiException(ErrorStatus._ACCESS_PERMISSION_DENIED);
        }

        //강의가 존재하는 지 확인
        Lecture lecture = lectureService.findById(lectureId);

        //강의가 승인이 되었는 지 확인
        if (!lecture.getApproval().equals(Approval.APPROVED)) {
            throw new ApiException(ErrorStatus._ACCESS_PERMISSION_DENIED);
        }

        //해당 영상이 있는 지 확인
        LectureVideo lectureVideo = lectureVideoRepository.findById(videoId).orElseThrow(() ->
                new ApiException(ErrorStatus._NOT_FOUND_LECTURE_VIDEO));

        //signedURL 생성
        String signedUrl = null;

        try {
            signedUrl = cloudFrontService.generateSignedUrl(lectureVideo.getFileName(), 60);
        } catch (Exception e) {
            throw new ApiException(ErrorStatus._INVALID_URL_FORMAT); //SignedURL 생성 실패
        }

        return GetLectureVideoDetailResponse.of(
                lectureVideo.getTitle(),
                signedUrl
        );
    }

    /**
     * 강의 영상 삭제
     *
     * @param authUser
     * @param lectureId
     * @param videoId
     * @return
     */
    public void deleteVideo(AuthUser authUser, Long lectureId, Long videoId) {
        //강의가 존재하는 지 확인
        Lecture lecture = lectureService.findById(lectureId);

        //해당 영상이 있는 지 확인
        LectureVideo lectureVideo = lectureVideoRepository.findById(videoId).orElseThrow(() ->
                new ApiException(ErrorStatus._NOT_FOUND_LECTURE_VIDEO));

        //영상을 등록한 유저가 맞는 지 확인
        if (!authUser.getId().equals(lecture.getUser().getId())) {
            throw new ApiException(ErrorStatus._ACCESS_PERMISSION_DENIED);
        }

        deleteLectureVideo(lectureVideo);
    }

    //Util
    public List<LectureVideo> findLectureVideoByLectureId(Long lectureId) {
        return lectureVideoRepository.findAllByLectureId(lectureId);
    }

    //영상 삭제
    public void deleteLectureVideo(LectureVideo lectureVideo) {
        //S3 영상 파일 삭제
        s3Service.deleteVideo(lectureVideo.getFileName());
        lectureVideoRepository.delete(lectureVideo);
    }
}
