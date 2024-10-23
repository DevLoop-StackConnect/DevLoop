package com.devloop.common.utils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Util {

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    private final AmazonS3Client amazonS3Client;

    public String makeFileName(MultipartFile file){
        return UUID.randomUUID() + file.getOriginalFilename();

    }

    public String uploadFile(MultipartFile file,String bucketName) {

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

    // 버킷 이름과 오리지널 파일이름으로 URL 반환하는 메서드
    public URL getUrl(String bucketName, String orginalFileName) {
        return amazonS3Client.getUrl(bucketName, orginalFileName);
    }
}
