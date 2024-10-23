package com.devloop.user.service;

import com.devloop.attachment.entity.ProfileAttachment;
import com.devloop.attachment.service.FAService;
import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.common.utils.S3Util;
import com.devloop.party.entity.Party;
import com.devloop.party.repository.PartyRepository;
import com.devloop.party.response.GetPartyListResponse;
import com.devloop.user.dto.response.UserResponse;
import com.devloop.user.entity.User;
import com.devloop.user.repository.UserRepository;
import com.devloop.attachment.repository.FARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final FARepository faRepository;
    private final PartyRepository partyRepository;
    private final S3Util s3Util;

    public UserResponse getUser(AuthUser authUser) {
        User user = userRepository.findById(authUser.getId()).orElseThrow(()->new ApiException(ErrorStatus._NOT_FOUND_USER));
        String imageURL = "https://devloop-stackconnect1.s3.ap-northeast-2.amazonaws.com/defaultImg.png";
        if(user.getAttachmentId() != null) {
            // 디폴트 이미지 아닐때 ->
            ProfileAttachment profileAttachment = faRepository.findById(user.getAttachmentId())
                    .orElseThrow(()->new ApiException(ErrorStatus._NOT_FOUND_));
            imageURL = profileAttachment.getImageURL();
        }
        Party party = partyRepository.findByUserId(user.getId());
        GetPartyListResponse getPartyListResponse = GetPartyListResponse.from(party);

        return UserResponse.from(user.getUsername(),user.getEmail(),user.getUserRole(),imageURL,getPartyListResponse);
    }

    @Transactional
    public void updateProfileImg(MultipartFile file, AuthUser authUser) {
        s3Util.uploadFile(file);
        User user = userRepository.findById(authUser.getId()).orElseThrow(()->new NoSuchElementException("User not found"));
        user.updateProfileImg(3L);
    }
}
