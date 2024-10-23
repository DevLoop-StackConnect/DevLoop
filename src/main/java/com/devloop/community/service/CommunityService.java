package com.devloop.community.service;

import com.devloop.common.AuthUser;
import com.devloop.community.dto.request.CommunitySaveRequest;
import com.devloop.community.dto.response.CommunitySaveResponse;
import com.devloop.community.entity.Community;
import com.devloop.community.repository.CommunityRepository;
import com.devloop.user.entity.User;
import com.devloop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    //게시글 작성
    @Transactional
    public CommunitySaveResponse createCommunity(AuthUser authUser, CommunitySaveRequest communitySaveRequest) {
        //사용자 조회
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(()->new IllegalArgumentException("사용자 찾을수업음"));
        //게시글 Community객체 생성
        Community community = Community.from(communitySaveRequest,user);
        //게시글 저장
        Community savedCommunity = communityRepository.save(community);
        //응답반환
        return new CommunitySaveResponse(
                savedCommunity.getId(),
                savedCommunity.getTitle(),
                savedCommunity.getContent(),
                savedCommunity.getResolveStatus(),
                savedCommunity.getCategory(),
                savedCommunity.getCreatedAt(),
                savedCommunity.getModifiedAt()
        );
    }
}
