package com.devloop.common.utils;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.devloop.attachment.entity.ProfileAttachment;
import com.devloop.attachment.enums.Domain;
import com.devloop.attachment.repository.FARepository;
import com.devloop.common.Validator.FileValidator;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Util {

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    private final AmazonS3Client amazonS3Client;
    private final FARepository faRepository;
    private final FileValidator fileValidator;

    public String makeFileName(MultipartFile file){
        return UUID.randomUUID() + file.getOriginalFilename();

    }

    public String uploadFile(MultipartFile file){
        String fileName = makeFileName(file);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        try {
            amazonS3Client.putObject(bucketName, fileName, file.getInputStream(), metadata);
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    // 유저 프로필 변경시 사용
    public void uploadFile(MultipartFile file, User user) {
        if(user.getAttachmentId() != null) {
            // 디폴트 이미지가 아닐때 S3에서 삭제
            ProfileAttachment currentImg = faRepository.findById(user.getAttachmentId())
                    .orElseThrow(()->new ApiException(ErrorStatus._ATTACHMENT_NOT_FOUND));
            String currentImgName = currentImg.getFileName();
            delete(currentImgName);
            faRepository.delete(currentImg);
        }
        String fileName = uploadFile(file);
        ProfileAttachment profileAttachment = ProfileAttachment.from(
                user.getId(),
                getUrl(file.getOriginalFilename()),
                fileValidator.mapStringToFileFormat(Objects.requireNonNull(file.getContentType())),
                Domain.PROFILE,
                fileName
        );
        faRepository.save(profileAttachment);
        user.updateProfileImg(profileAttachment.getId());;
    }

        public void delete(String fileName){
        if(amazonS3Client.doesObjectExist(bucketName, fileName)) {
            try {
                amazonS3Client.deleteObject(new DeleteObjectRequest(bucketName,fileName));


            } catch (AmazonServiceException e) {
                throw new ApiException(ErrorStatus._HAS_NOT_ACCESS_PERMISSION);
            }
        } else {
            throw new ApiException(ErrorStatus._ATTACHMENT_NOT_FOUND);
        }
    }

    // 버킷 이름과 오리지널 파일이름으로 URL 반환하는 메서드
    public URL getUrl(String orginalFileName) {
        return amazonS3Client.getUrl(bucketName, orginalFileName);
    }

    public String extractFileNameFromS3Url(URL s3Url) {
        String path = s3Url.getPath();
        String[] parts = path.split("/");
        return parts[parts.length - 1];

    }
}
