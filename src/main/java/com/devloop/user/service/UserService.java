package com.devloop.user.service;

import com.devloop.attachment.entity.ProfileAttachment;
import com.devloop.attachment.repository.ProfileATMRepository;
import com.devloop.attachment.s3.S3Service;
import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.community.entity.Community;
import com.devloop.community.response.CommunitySimpleResponse;
import com.devloop.community.service.CommunityService;
import com.devloop.party.entity.Party;
import com.devloop.party.response.GetPartyListResponse;
import com.devloop.party.service.PartyService;
import com.devloop.tutor.entity.TutorRequest;
import com.devloop.tutor.service.TutorService;
import com.devloop.user.entity.User;
import com.devloop.user.repository.UserRepository;
import com.devloop.user.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final ProfileATMRepository profileATMRepository;
    private final PartyService partyService;
    private final CommunityService communityService;
    private final TutorService tutorService;
    private final S3Service s3Service;

    public UserResponse getUser(AuthUser authUser) {
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_USER));
        try {
            URL imageURL = new URL("https://devloop-stackconnect1.s3.ap-northeast-2.amazonaws.com/defaultImg.png");
            if (user.getAttachmentId() != null) {
                // 디폴트 이미지 아닐때 ->
                ProfileAttachment profileAttachment = profileATMRepository.findById(user.getAttachmentId())
                        .orElseThrow(() -> new ApiException(ErrorStatus._ATTACHMENT_NOT_FOUND));
                imageURL = profileAttachment.getImageURL();
            }
            /*
             * 유저 개인 프로필에 보여줄 참여중인 스터디 리스트
             * */
            List<GetPartyListResponse> userPartyList = userPartyResponses(user);
            /*
             * 유저 개인 프로필에 보여줄 작성한 커뮤니티 게시글 리스트
             * */
            List<CommunitySimpleResponse> userCommunityPost = userCommunityResponses(user);
            /*
             * 유저 개인 프로필에 보여줄 튜터 신청서 url
             * */
            TutorRequest tutorRequest = tutorService.getTutorRequestByUserId(user.getId());
            return UserResponse.of(
                    user.getUsername(),
                    user.getEmail(),
                    user.getUserRole().toString(),
                    imageURL,
                    userPartyList,
                    userCommunityPost,
                    tutorRequest != null ? tutorRequest.getSubUrl() : null);
        } catch (MalformedURLException i) {
            throw new RuntimeException();
        }
    }

    @Transactional
    public void updateProfileImg(MultipartFile[] files, AuthUser authUser) {
        if (files.length != 1) {
            throw new ApiException(ErrorStatus._FILE_ISNOT_ONE);
        }
        MultipartFile file = files[0];
        User user = userRepository.findById(authUser.getId()).orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_USER));
        if (user.getAttachmentId() != null) {
            // 디폴트 이미지가 아닐때 S3에서 삭제
            ProfileAttachment currentImg = profileATMRepository.findById(user.getAttachmentId())
                    .orElseThrow(() -> new ApiException(ErrorStatus._ATTACHMENT_NOT_FOUND));
            String currentImgName = currentImg.getFileName();
            s3Service.delete(currentImgName);
            profileATMRepository.delete(currentImg);
        }
        s3Service.uploadFile(file, user, user);
    }

    //---------------------------------------------------util---------------------------------------------------//
    public User findByUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_USER));
    }

    /*
     * 유저 개인 프로필에 보여줄 참여중인 스터디 리스트
     * */
    public List<GetPartyListResponse> userPartyResponses(User user) {

        List<Party> partys = partyService.getPartiesByUserId(user.getId());
        List<GetPartyListResponse> getPartyListResponses = new ArrayList<>();
        if (partys != null) {
            //유저가 소속된 파티 있을 때
            for (Party party : partys) {
                GetPartyListResponse getPartyListResponse = GetPartyListResponse.of(
                        party.getId(),
                        party.getTitle(),
                        party.getContents(),
                        party.getStatus().getStatus(),
                        party.getCategory().getDescription()
                );
                getPartyListResponses.add(getPartyListResponse);
            }
            return getPartyListResponses;
        }
        //유저가 소속된 파티 없을 때
        return new ArrayList<>();
    }

    public List<CommunitySimpleResponse> userCommunityResponses(User user) {
        List<Community> communities = communityService.getCommunitiesByUserId(user.getId());
        if (communities != null) {
            //유저가 올린 커뮤니티 게시글 있을 때
            List<CommunitySimpleResponse> communitySimpleResponses = new ArrayList<>();
            for (Community community : communities) {
                CommunitySimpleResponse communitySimpleResponse = CommunitySimpleResponse.of(
                        community.getId(),
                        community.getTitle(),
                        community.getResolveStatus().getDescription(), //.getDescription 추가
                        community.getCategory().getDescription() //.getDescription 추가
                );
                communitySimpleResponses.add(communitySimpleResponse);
            }
            return communitySimpleResponses;
        }
        //유저가 올린 커뮤니티 게시글 없을 때
        return new ArrayList<>();
    }
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ApiException(ErrorStatus._NOT_FOUND_USER)
        );
    }
    public Optional<User> getUserEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public User save(User user) {
        return userRepository.save(user);
    }
}
