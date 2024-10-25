package com.devloop.attachment.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.devloop.attachment.entity.CommunityAttachment;
import com.devloop.attachment.entity.PartyAttachment;
import com.devloop.attachment.entity.ProfileAttachment;
import com.devloop.attachment.enums.FileFormat;
import com.devloop.attachment.repository.CommunityATMRepository;
import com.devloop.attachment.repository.PartyAMTRepository;
import com.devloop.attachment.repository.ProfileATMRepository;
import com.devloop.common.Validator.FileValidator;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.community.entity.Community;
import com.devloop.community.repository.CommunityRepository;
import com.devloop.party.entity.Party;
import com.devloop.party.repository.PartyRepository;
import com.devloop.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class S3Service {

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;
    private final AmazonS3Client amazonS3Client;
    private final CommunityRepository communityRepository;
    private final PartyAMTRepository partyAMTRepository;
    private final CommunityATMRepository communityATMRepository;
    private final FileValidator fileValidator;
    private final ProfileATMRepository profileATMRepository;

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

    public <T> void uploadFile(MultipartFile file, User user, T object){

        fileValidator.fileTypeValidator(file,object);
        FileFormat fileType =  fileValidator.mapStringToFileFormat(Objects.requireNonNull(file.getContentType()));
        String fileName = makeFileName(file);
        URL url = getUrl(file.getOriginalFilename());

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());


        try {
            amazonS3Client.putObject(bucketName, fileName, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (object instanceof Party) {
            PartyAttachment partyAttachment = PartyAttachment.of(
                    user.getId(),
                    url,
                    fileType,
                    fileName
            );
            partyAMTRepository.save(partyAttachment);
        } else if (object instanceof Community) {
            CommunityAttachment communityAttachment = CommunityAttachment.of(
                    user.getId(),
                    url,
                    fileType,
                    fileName
            );
            communityATMRepository.save(communityAttachment);
        } else if (object instanceof User) {
            ProfileAttachment profileAttachment = ProfileAttachment.of(
                    user.getId(),
                    url,
                    fileType,
                    fileName
            );
            profileATMRepository.save(profileAttachment);
            user.updateProfileImg(profileAttachment.getId());
        }
        /*else if (object instanceof PWT) {
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
