package com.devloop.user.service;

import com.devloop.attachment.entity.ProfileAttachment;
import com.devloop.attachment.repository.ProfileATMRepository;
import com.devloop.attachment.s3.S3Service;
import com.devloop.common.AuthUser;
import com.devloop.common.Validator.FileValidator;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.community.dto.response.CommunitySimpleResponse;
import com.devloop.community.entity.Community;
import com.devloop.community.repository.CommunityRepository;
import com.devloop.party.entity.Party;
import com.devloop.party.repository.PartyRepository;
import com.devloop.party.response.GetPartyListResponse;
import com.devloop.tutor.entity.TutorRequest;
import com.devloop.tutor.repository.TutorRequestRepository;
import com.devloop.user.dto.response.UserResponse;
import com.devloop.user.entity.User;
import com.devloop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
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
        /*
        * 유저 개인 프로필에 보여줄 참여중인 스터디 리스트
        * */

        List<Party> partys = partyRepository.findAllByUserId(user.getId())
                .orElseThrow(()->new ApiException(ErrorStatus._INVALID_REQUEST));
        List<GetPartyListResponse> GetPartyListResponses = new ArrayList<>();
        for (Party party : partys) {
            GetPartyListResponse getPartyListResponses = GetPartyListResponse.of(
                    party.getId(),
                    party.getTitle(),
                    party.getContents(),
                    party.getStatus().getStatus(),
                    party.getCategory().getDescription()
            );
            GetPartyListResponses.add(getPartyListResponses);
        }
        log.info(partys.toString());
        /*
        * 유저 개인 프로필에 보여줄 작성한 커뮤니티 게시글 리스트
        * */

        List<Community> communities = communityRepository.findAllByUserId(user.getId())
                .orElseThrow(()->new ApiException(ErrorStatus._INVALID_REQUEST));
        log.info(communities.toString());
        List<CommunitySimpleResponse> communitySimpleResponses = new ArrayList<>();
        for (Community community : communities) {
            CommunitySimpleResponse communitySimpleResponse = CommunitySimpleResponse.of(
                    community.getId(),
                    community.getTitle(),
                    community.getResolveStatus(),
                    community.getCategory()
            );
            communitySimpleResponses.add(communitySimpleResponse);
        }
        log.info(communities.toString());

        /*TutorRequest tutorRequest = tutorRequestRepository.findByUserId(user.getId())
                .orElseThrow(()-> new ApiException(ErrorStatus._UNSUPPORTED_OBJECT_TYPE));
*/

        return UserResponse.of(
                user.getUsername(),
                user.getEmail(),
                user.getUserRole().toString(),
                imageURL,
                GetPartyListResponses,
                communitySimpleResponses/*,
                tutorRequest.getSubUrl()*/);
    }

    @Transactional
    public void updateProfileImg(MultipartFile[] files, AuthUser authUser) {

        if(files.length != 1){
            throw new ApiException(ErrorStatus._FILE_ISNOT_ONE);
        }
        MultipartFile file = files[0];
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
