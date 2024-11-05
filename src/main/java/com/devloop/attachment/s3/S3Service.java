package com.devloop.attachment.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.devloop.attachment.entity.*;
import com.devloop.attachment.enums.FileFormat;
import com.devloop.attachment.repository.CommunityATMRepository;
import com.devloop.attachment.repository.PWTATMRepository;
import com.devloop.attachment.repository.PartyAMTRepository;
import com.devloop.attachment.repository.ProfileATMRepository;
import com.devloop.common.Validator.FileValidator;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.community.entity.Community;
import com.devloop.party.entity.Party;
import com.devloop.pwt.entity.ProjectWithTutor;
import com.devloop.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class S3Service {

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;
    private final AmazonS3Client amazonS3Client;
    private final PartyAMTRepository partyAMTRepository;
    private final CommunityATMRepository communityATMRepository;
    private final FileValidator fileValidator;
    private final ProfileATMRepository profileATMRepository;
    private final PWTATMRepository pwtATMRepository;
    @Value("${cloud.aws.cloudfront.cloudFrontUrl}")
    private String CLOUD_FRONT_URL;

    public String makeFileName(MultipartFile file){
        return  UUID.randomUUID() + file.getOriginalFilename();
    }

    public <T> void uploadFile(MultipartFile file, User user, T object){

        fileValidator.fileTypeValidator(file,object);
        FileFormat fileType = fileValidator.mapStringToFileFormat(Objects.requireNonNull(file.getContentType()));

        String fileName = makeFileName(file);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());


        try {
            amazonS3Client.putObject(bucketName, fileName, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try{
            URL url = new URL(CLOUD_FRONT_URL + fileName);
            if (object instanceof Party) {
                PartyAttachment partyAttachment = PartyAttachment.of(
                        ((Party) object).getId(),
                        url,
                        fileType,
                        fileName
                );
                partyAMTRepository.save(partyAttachment);
            } else if (object instanceof Community) {
                CommunityAttachment communityAttachment = CommunityAttachment.of(
                        ((Community) object).getId(),
                        url,
                        fileType,
                        fileName
                );
                communityATMRepository.save(communityAttachment);
            } else if (object instanceof User) {
                ProfileAttachment profileAttachment = ProfileAttachment.of(
                        ((User) object).getId(),
                        url,
                        fileType,
                        fileName
                );
                profileATMRepository.save(profileAttachment);
                user.updateProfileImg(profileAttachment.getId());
            }
            else if (object instanceof ProjectWithTutor) {
                PWTAttachment pwtAttachment = PWTAttachment.of(
                        ((ProjectWithTutor) object).getId(),
                        url,
                        fileType,
                        fileName
                );
                pwtATMRepository.save(pwtAttachment);
            }
            else {
                throw new ApiException(ErrorStatus._UNSUPPORTED_OBJECT_TYPE);
            }
        } catch (MalformedURLException ignored) {}


    }

    // 첨부파일 업데이트
    public <T extends Attachment, U> void updateUploadFile(MultipartFile file, T attachment, U object) {
        // 기존 S3 첨부파일 삭제
        delete(attachment.getFileName());

        // 새로운 첨부파일 file S3에 업로드
        fileValidator.fileTypeValidator(file, object);
        FileFormat fileType = fileValidator.mapStringToFileFormat(Objects.requireNonNull(file.getContentType()));

        String fileName = makeFileName(file);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        try {
            amazonS3Client.putObject(bucketName, fileName, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try{
            URL url = new URL(CLOUD_FRONT_URL+fileName);

            // 업로드한 S3파일을 기존 첨부파일 로컬 DB에 업데이트
            attachment.updateAttachment(url, fileType, fileName);
        } catch (MalformedURLException ignored) {}
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
}
