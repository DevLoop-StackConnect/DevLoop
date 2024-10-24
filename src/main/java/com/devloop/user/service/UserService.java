package com.devloop.user.service;

import com.devloop.attachment.entity.ProfileAttachment;
import com.devloop.attachment.enums.Domain;
import com.devloop.attachment.repository.FARepository;
import com.devloop.common.AuthUser;
import com.devloop.common.Validator.FileValidator;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.party.entity.Party;
import com.devloop.party.repository.PartyRepository;
import com.devloop.party.response.GetPartyListResponse;
import com.devloop.s3.S3Service;
import com.devloop.user.dto.response.UserResponse;
import com.devloop.user.entity.User;
import com.devloop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final FARepository faRepository;
    private final PartyRepository partyRepository;
    private final S3Service s3Service;
    private final FileValidator fileValidator;

    public UserResponse getUser(AuthUser authUser) throws MalformedURLException {

        User user = userRepository.findById(authUser.getId())
                        .orElseThrow(()->new ApiException(ErrorStatus._NOT_FOUND_USER));

        URL imageURL = new URL("https://devloop-stackconnect1.s3.ap-northeast-2.amazonaws.com/defaultImg.png");

        if(user.getAttachmentId() != null) {
            // 디폴트 이미지 아닐때 ->
            ProfileAttachment profileAttachment = faRepository.findById(user.getAttachmentId())
                    .orElseThrow(()->new ApiException(ErrorStatus._ATTACHMENT_NOT_FOUND));
            imageURL = profileAttachment.getImageURL();
        }

        Party party = partyRepository.findByUserId(user.getId());
        GetPartyListResponse getPartyListResponse = GetPartyListResponse.from(party);
        return UserResponse.of(user.getUsername(),user.getEmail(),user.getUserRole(),imageURL,getPartyListResponse);
    }

    @Transactional
    public void updateProfileImg(MultipartFile file, AuthUser authUser) {
        User user = userRepository.findById(authUser.getId()).orElseThrow(()->new ApiException(ErrorStatus._NOT_FOUND_USER));
        if(user.getAttachmentId() != null) {
            // 디폴트 이미지가 아닐때 S3에서 삭제
            ProfileAttachment currentImg = faRepository.findById(user.getAttachmentId())
                    .orElseThrow(()->new ApiException(ErrorStatus._ATTACHMENT_NOT_FOUND));
            String currentImgName = currentImg.getFileName();
            s3Service.delete(currentImgName);
            faRepository.delete(currentImg);
        }
        String fileName = s3Service.uploadFile(file);
        ProfileAttachment profileAttachment = ProfileAttachment.of(
                user.getId(),
                s3Service.getUrl(file.getOriginalFilename()),
                fileValidator.mapStringToFileFormat(Objects.requireNonNull(file.getContentType())),
                Domain.PROFILE,
                fileName
        );
        faRepository.save(profileAttachment);
        user.updateProfileImg(profileAttachment.getId());
    }
    //----------------------------------------------------util---------------------------------------------------//
    public User findByUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(()->new ApiException(ErrorStatus._NOT_FOUND_USER));
    }
}
