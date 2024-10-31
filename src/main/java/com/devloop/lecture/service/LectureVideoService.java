package com.devloop.lecture.service;

import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.lecture.entity.Lecture;
import com.devloop.lecture.entity.LectureVideo;
import com.devloop.lecture.enums.VideoStatus;
import com.devloop.lecture.repository.LectureRepository;
import com.devloop.lecture.repository.LectureVideoRepository;
import com.devloop.lecture.request.MultipartUploadCompleteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.UploadPartPresignRequest;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LectureVideoService {
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final LectureRepository lectureRepository;
    private final LectureVideoRepository lectureVideoRepository;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

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

    //각 파트 별 PresignedUrl 반환
    public Map<Integer, String> getPresignedUrlsForParts(String uploadId, String fileName, int partCount) {
        //파트 번호에 대한 PresignedUrl
        Map<Integer,String> presignedUrls=new HashMap<>();

        for(int i=1;i<=partCount;i++){
            //각 파트에 대한 객체 생성
            UploadPartRequest uploadPartRequest=UploadPartRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .uploadId(uploadId)
                    .partNumber(i)
                    .build();

            //UploadPartRequest를 기반으로 프리사인 URL을 생성하기 위한 객체 생성
            UploadPartPresignRequest uploadPartPresignRequest=UploadPartPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(15)) //프리사인 URL의 유효기간
                    .uploadPartRequest(uploadPartRequest) //특정 파트에 사용된다는 것을 지정
                    .build();

            //프리사인 URL생성, URL 객체로 반환
            URL presignedUrl=s3Presigner.presignUploadPart(uploadPartPresignRequest).url();
            presignedUrls.put(i,presignedUrl.toString());
        }
        return presignedUrls;
    }

    //Multipart Upload 완료
    public String completeMultipartUpload(Long lectureId, MultipartUploadCompleteRequest uploadCompleteRequest) throws MalformedURLException {
        //강의가 존재하는 지 확인
        Lecture lecture=lectureRepository.findById(lectureId).orElseThrow(()->
                new ApiException(ErrorStatus._NOT_FOUND_Lecture));

        //각 파트를 CompletedPart 객체로 변환, 멀티파트 업로드를 완료할 때 필요한 데이터
        List<CompletedPart> completedParts=uploadCompleteRequest.getParts().stream()
                .map(part-> CompletedPart.builder()
                        .partNumber(part.getPartNumber())
                        .eTag(part.getETag())
                        .build())
                .toList();

        //CompletedPart 객체들을 리스트로 포함하는 객체 생성, 업로드 완료 요청 시 필요한 정보를 담고 있음
        CompletedMultipartUpload multipartUpload=CompletedMultipartUpload.builder()
                .parts(completedParts)
                .build();

        //S3에 최종적으로 업로드 완료 요청을 보내기 위한 준비
        CompleteMultipartUploadRequest completeMultipartUploadRequest=CompleteMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(uploadCompleteRequest.getFileName())
                .uploadId(uploadCompleteRequest.getUploadId())
                .multipartUpload(multipartUpload)
                .build();

        try{
            //S3에 업로드 완료 요청
            s3Client.completeMultipartUpload(completeMultipartUploadRequest);

            //강의 영상 첨부파일 DB 저장
            String s3Url = "https://" + bucketName + ".s3.amazonaws.com/" + uploadCompleteRequest.getFileName();

            LectureVideo lectureVideo=LectureVideo.of(
                    new URL(s3Url),
                    uploadCompleteRequest.getFileName(),
                    VideoStatus.COMPLETED,
                    lecture
            );
            lectureVideoRepository.save(lectureVideo);

            return s3Url;

        }catch (S3Exception e){
            throw new ApiException(ErrorStatus._S3_UPLOAD_ERROR);
        }

    }
}
