package com.devloop.attachment.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.community.entity.Community;
import com.devloop.community.repository.CommunityRepository;
import com.devloop.party.entity.Party;
import com.devloop.party.repository.PartyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class S3Service {

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;
    private final AmazonS3Client amazonS3Client;
    private final CommunityRepository communityRepository;
    private final PartyRepository partyRepository;

    public String makeFileName(MultipartFile file){
        return UUID.randomUUID() + file.getOriginalFilename();
    }

    //유저 프로필에서 사용하는 업로드 메서드
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

    public <T> void uploadFile(MultipartFile file, T object){
        String fileName = makeFileName(file);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        try {
            amazonS3Client.putObject(bucketName, fileName, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (object instanceof Party) {
            partyRepository.save((Party) object);
        } else if (object instanceof Community) {
            communityRepository.save((Community) object);
        } /*else if (object instanceof PWT) {
            return 3;
        } */
        else {
            throw new ApiException(ErrorStatus._UNSUPPORTED_OBJECT_TYPE);
        }
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
    public URL getUrl(String originalFileName) {
        return amazonS3Client.getUrl(bucketName, originalFileName);
    }

    public String extractFileNameFromS3Url(URL s3Url) {
        String path = s3Url.getPath();
        String[] parts = path.split("/");
        return parts[parts.length - 1];
    }
}
