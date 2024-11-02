package com.devloop.lecture.service;

import com.devloop.attachment.cloudfront.CloudFrontService;
import com.devloop.attachment.enums.FileFormat;
import com.devloop.attachment.s3.S3Service;
import com.devloop.common.AuthUser;
import com.devloop.common.Validator.FileValidator;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.enums.Approval;
import com.devloop.common.exception.ApiException;
import com.devloop.lecture.entity.Lecture;
import com.devloop.lecture.entity.LectureVideo;
import com.devloop.lecture.enums.VideoStatus;
import com.devloop.lecture.repository.LectureVideoRepository;
import com.devloop.lecture.response.GetLectureVideoDetailResponse;
import com.devloop.lecture.response.GetLectureVideoListResponse;
import com.devloop.user.entity.User;
import com.devloop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class LectureVideoService {
    private final S3Client s3Client;
    private final LectureVideoRepository lectureVideoRepository;
    private final UserService userService;
    private final S3Service s3Service;
    private final CloudFrontService cloudFrontService;
    private final LectureService lectureService;
    private final FileValidator fileValidator;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    /**
     * 멀티파트 파일 업로드
     * @param authUser
     * @param lectureId
     * @param multipartFile
     * @param title
     * @return
     * @throws IOException
     */
    public String uploadLectureVideo(AuthUser authUser,Long lectureId, MultipartFile multipartFile, String title) throws IOException {
        //유저가 존재하는 지 확인
        User user= userService.findByUserId(authUser.getId());

        //강의가 존재하는 지 확인
        Lecture lecture=lectureService.findById(lectureId);

        //유저가 강의 등록한 유저인지 확인
        if(!user.getId().equals(lecture.getUser().getId())){
            throw new ApiException(ErrorStatus._HAS_NOT_ACCESS_PERMISSION);
        }

        //파일 타입 확인
        fileValidator.fileTypeValidator(multipartFile,lecture);
        FileFormat fileType=fileValidator.mapStringToFileFormat(Objects.requireNonNull(multipartFile.getContentType()));

        //파일 사이즈 확인 (임시로 1GB까지 가능)
        fileValidator.fileSizeValidator(multipartFile,1L*1024*1024*1024);

        //MultipartFile을 File로 변환
        File file=convertMultipartFileToFile(multipartFile);

        long fileSize=file.length();

        String folderPath="lectures/"+lectureId+"/";
        String fileName = makeFileName(folderPath,multipartFile);
        long partSize=5 * 1024 * 1024; //5MB 단위로 파트 분할
        String uploadId=null;
        List<CompletedPart> completedParts=new ArrayList<>();

        //멀티파트 업로드 시작 요청 및 UploadId 반환
        uploadId=getUploadId(fileName);

        //파일을 partSize만큼 파트로 나누어 업로드
        long filePosition=0;

        try(FileInputStream inputStream=new FileInputStream(file)){
            for(int i=1;filePosition<fileSize;i++){
                //마지막 파트 크기가 partSize 미만일 경우 조정
                long currentPartSize=Math.min(partSize,(fileSize-filePosition));
                log.info("currentPartSize={}",currentPartSize);

                //각 파트에 대한 객체 생성
                UploadPartRequest uploadPartRequest=UploadPartRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .uploadId(uploadId)
                        .partNumber(i)
                        .build();

                //파일의 특정 위치에서 InputStream을 읽어 RequestBody 생성
                byte[] buffer=new byte[(int) currentPartSize];
                inputStream.read(buffer,0,(int)currentPartSize);

                //각 파트를 업로드하고 ETag를 partETags에 추가
                UploadPartResponse uploadPartResponse=s3Client.uploadPart(uploadPartRequest, RequestBody.fromBytes(buffer));
                CompletedPart part=CompletedPart.builder()
                        .partNumber(i)
                        .eTag(uploadPartResponse.eTag())
                        .build();

                completedParts.add(part);

                filePosition+=currentPartSize;
            }

            //멀티 파트 업로드 완료 요청
            CompleteMultipartUploadRequest completeMultipartUploadRequest=CompleteMultipartUploadRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .uploadId(uploadId)
                    .multipartUpload(CompletedMultipartUpload.builder().parts(completedParts).build())
                    .build();

            s3Client.completeMultipartUpload(completeMultipartUploadRequest);


            //새로운 강의 영상 객체 생성
            LectureVideo lectureVideo=LectureVideo.of(
                    fileName,
                    title,
                    VideoStatus.COMPLETED,
                    fileType,
                    lecture
            );
            lectureVideoRepository.save(lectureVideo);

            return fileName;

        }catch (S3Exception e){
            //예외 발생 시, 업로드 취소
            s3Client.abortMultipartUpload(AbortMultipartUploadRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .uploadId(uploadId)
                            .build());

            throw new ApiException(ErrorStatus._S3_UPLOAD_ERROR);
        }

    }

    // MultipartFile을 File로 변환
    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        //임시 파일 생성
        File file = File.createTempFile("temp",multipartFile.getOriginalFilename());
        multipartFile.transferTo(file);
        file.deleteOnExit(); //임시 파일 삭제 예약
        return file;
    }
    //파일 이름 생성
    public String makeFileName(String folderPath,MultipartFile multipartFile){
        return folderPath+UUID.randomUUID() +"_"+ multipartFile.getOriginalFilename();
    }

    //Upload Id 반환
    public String getUploadId(String fileName) {
        //어떤 bucket에 어떤 object를 업로드할 것인지에 대한 Request 정보 생성하기 위해 MultipartUploadRequest 빌드하여 객체 생성
        CreateMultipartUploadRequest request=CreateMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        //생성된 객체를 createMultipartUpload 메서드에 전달
        CreateMultipartUploadResponse response=s3Client.createMultipartUpload(request);

        return response.uploadId();
    }

    /**
     * 강의 영상 다건 조회
     * @param authUser
     * @param lectureId
     * @return
     */
    public List<GetLectureVideoListResponse> getLectureVideoList(AuthUser authUser, Long lectureId) {
        //강의가 존재하는 지 확인
        Lecture lecture=lectureService.findById(lectureId);

        //강의가 승인이 되었는 지 확인
        if(!lecture.getApproval().equals(Approval.APPROVED)){
            throw new ApiException(ErrorStatus._ACCESS_PERMISSION_DENIED);
        }

        List<LectureVideo> lectureVideos=lectureVideoRepository.findAllByLectureId(lectureId);

        return lectureVideos.stream().map(lectureVideo -> {
            return GetLectureVideoListResponse.of(
                    lectureVideo.getId(),
                    lectureVideo.getTitle()
            );
        }).toList();
    }

    /**
     * 강의 영상 단건 조회 (수강 유저만 조회 가능)
     * @param authUser
     * @param videoId
     * @return
     */
    public GetLectureVideoDetailResponse getLectureVideo(AuthUser authUser, Long videoId) throws Exception {
        //수강 유저인지 확인

        //해당 영상이 있는 지 확인
        LectureVideo lectureVideo=lectureVideoRepository.findById(videoId).orElseThrow(()->
                new ApiException(ErrorStatus._NOT_FOUND_LECTURE_VIDEO));

        //signedURL 생성
        String signedUrl=cloudFrontService.generateSignedUrl(lectureVideo.getFileName(),60);

        return GetLectureVideoDetailResponse.of(
                lectureVideo.getTitle(),
                new URL(signedUrl)
        );
    }

    /**
     * 강의 영상 삭제
     * @param authUser
     * @param videoId
     * @return
     */
    public String deleteVideo(AuthUser authUser, Long videoId) {
        //해당 영상이 있는 지 확인
        LectureVideo lectureVideo=lectureVideoRepository.findById(videoId).orElseThrow(()->
                new ApiException(ErrorStatus._NOT_FOUND_LECTURE_VIDEO));

        //영상을 등록한 유저가 맞는 지 확인

        deleteLectureVideo(lectureVideo);

        return String.format("%s 강의를 삭제하였습니다.", lectureVideo.getTitle());
    }

    //Util
    public List<LectureVideo> findLectureVideoByLectureId(Long lectureId){
       return lectureVideoRepository.findAllByLectureId(lectureId);
    }

    //영상 삭제
    public void deleteLectureVideo(LectureVideo lectureVideo){
        //S3 영상 파일 삭제
        s3Service.delete(lectureVideo.getFileName());
        lectureVideoRepository.delete(lectureVideo);
    }



}
