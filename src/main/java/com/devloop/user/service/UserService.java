package com.devloop.user.service;

import com.devloop.attachment.entity.ProfileAttachment;
import com.devloop.attachment.enums.FileFormat;
import com.devloop.attachment.repository.ProfileATMRepository;
import com.devloop.attachment.s3.S3Service;
import com.devloop.common.AuthUser;
import com.devloop.common.Validator.FileValidator;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.community.repository.CommunityRepository;
import com.devloop.party.entity.Party;
import com.devloop.party.repository.PartyRepository;
import com.devloop.party.response.GetPartyListResponse;
import com.devloop.tutor.repository.TutorRequestRepository;
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
    private final ProfileATMRepository profileATMRepository;
    private final PartyRepository partyRepository;
    private final CommunityRepository communityRepository;
    private final TutorRequestRepository tutorRequestRepository;
    private final S3Service s3Service;
    private final FileValidator fileValidator;

    public UserResponse getUser(AuthUser authUser) throws MalformedURLException {

        User user = userRepository.findById(authUser.getId())
                        .orElseThrow(()->new ApiException(ErrorStatus._NOT_FOUND_USER));

        URL imageURL = new URL("https://devloop-stackconnect1.s3.ap-northeast-2.amazonaws.com/defaultImg.png");

        if(user.getAttachmentId() != null) {
            // 디폴트 이미지 아닐때 ->
            ProfileAttachment profileAttachment = profileATMRepository.findById(user.getAttachmentId())
                    .orElseThrow(()->new ApiException(ErrorStatus._ATTACHMENT_NOT_FOUND));
            imageURL = profileAttachment.getImageURL();
        }

        Party party = partyRepository.findByUserId(user.getId())
                .orElseThrow(()->new ApiException(ErrorStatus._NOT_FOUND_PARTY));
        GetPartyListResponse getPartyListResponse = GetPartyListResponse.of(
                party.getId(),
                party.getTitle(),
                party.getContents(),
                party.getStatus().getStatus(),
                party.getCategory().getDescription()
        );

        /*Community community = communityRepository.findByUserId(user.getId())
                .orElseThrow(()->new ApiException(ErrorStatus._NOT_FOUND_COMMUNITY));
        CommunitySimpleResponse communitySimpleResponse = CommunitySimpleResponse.from(community);

        TutorRequest tutorRequest = tutorRequestRepository.findByUserId(user.getId())
                .orElseThrow(()-> new ApiException(ErrorStatus._UNSUPPORTED_OBJECT_TYPE));*/


        return UserResponse.of(
                user.getUsername(),
                user.getEmail(),
                user.getUserRole().toString(),
                imageURL,
                getPartyListResponse/*,
                communitySimpleResponse,
                tutorRequest.getSubUrl()*/);
    }

    @Transactional
    public void updateProfileImg(MultipartFile file, AuthUser authUser) {
        User user = userRepository.findById(authUser.getId()).orElseThrow(()->new ApiException(ErrorStatus._NOT_FOUND_USER));

        if(user.getAttachmentId() != null) {
            // 디폴트 이미지가 아닐때 S3에서 삭제
            ProfileAttachment currentImg = profileATMRepository.findById(user.getAttachmentId())
                    .orElseThrow(()->new ApiException(ErrorStatus._ATTACHMENT_NOT_FOUND));
            String currentImgName = currentImg.getFileName();
            s3Service.delete(currentImgName);
            profileATMRepository.delete(currentImg);
        }
        s3Service.uploadFile(file,user,user);
    }
    //----------------------------------------------------util---------------------------------------------------//
    public User findByUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(()->new ApiException(ErrorStatus._NOT_FOUND_USER));
    }
}
