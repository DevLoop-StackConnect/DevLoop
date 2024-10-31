package com.devloop.lecture.service;

import com.amazonaws.AmazonServiceException;
import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.lecture.entity.Lecture;
import com.devloop.lecture.entity.LectureVideo;
import com.devloop.lecture.enums.VideoStatus;
import com.devloop.lecture.repository.LectureRepository;
import com.devloop.lecture.repository.LectureVideoRepository;
import com.devloop.user.entity.User;
import com.devloop.user.enums.UserRole;
import com.devloop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

@Service
@RequiredArgsConstructor
public class LectureVideoService {
    private final S3Client s3Client;
    private final LectureRepository lectureRepository;
    private final LectureVideoRepository lectureVideoRepository;
    private final UserService userService;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    //멀티 파트 파일 업로드
    public String uploadVideo(AuthUser authUser,Long lectureId, MultipartFile multipartFile) throws IOException {
        //유저가 존재하는 지 확인
        User user= userService.findByUserId(authUser.getId());

        //강의가 존재하는 지 확인
        Lecture lecture=lectureRepository.findById(lectureId).orElseThrow(()->
                new ApiException(ErrorStatus._NOT_FOUND_LECTURE));

        //유저가 강의 등록한 유저인지 확인
        if(user.getId().equals(lecture.getUser().getId())){
            throw new ApiException(ErrorStatus._HAS_NOT_ACCESS_PERMISSION);
        }

        //MultipartFile을 File로 변환
        File file=convertMultipartFileToFile(multipartFile);

        long fileSize=file.length();
        String fileName = makeFileName(multipartFile);
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

            //강의 영상 첨부파일 DB 저장
            String s3Url = "https://" + bucketName + ".s3.amazonaws.com/" + fileName;

            LectureVideo lectureVideo=LectureVideo.of(
                    new URL(s3Url),
                    fileName,
                    VideoStatus.COMPLETED,
                    lecture
            );
            lectureVideoRepository.save(lectureVideo);

            return s3Url;

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
    public String makeFileName(MultipartFile multipartFile){
        return UUID.randomUUID() +"_"+ multipartFile.getOriginalFilename();
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


    //Util
    public Optional<List<LectureVideo>> findLectureVideoByLectureId(Long lectureId){
       return lectureVideoRepository.findAllByLectureId(lectureId);
    }

    public void deleteLectureVideo(LectureVideo lectureVideo){
        lectureVideoRepository.delete(lectureVideo);
    }

}
