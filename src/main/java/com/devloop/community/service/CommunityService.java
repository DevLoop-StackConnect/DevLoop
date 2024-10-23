package com.devloop.community.service;

import com.devloop.common.AuthUser;
import com.devloop.community.dto.request.CommunitySaveRequest;
import com.devloop.community.dto.response.CommunityDetailResponse;
import com.devloop.community.dto.response.CommunitySaveResponse;
import com.devloop.community.dto.response.CommunitySimpleResponse;
import com.devloop.community.entity.Community;
import com.devloop.community.repository.CommunityRepository;
import com.devloop.user.entity.User;
import com.devloop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

    //게시글 다건 조회
    public Page<CommunitySimpleResponse> getCommunities(Pageable pageable) {
        //페이지네이션된 게시글 조회
        Page<Community> communities = communityRepository.findAll(pageable);

        List<CommunitySimpleResponse> responseList = new ArrayList<>();
        //응답반환
        for (Community community : communities){
            CommunitySimpleResponse response = new CommunitySimpleResponse(
                    community.getId(),
                    community.getTitle(),
                    community.getCreatedAt(),
                    community.getModifiedAt(),
                    community.getResolveStatus(),
                    community.getCategory()
            );
            responseList.add(response);
        }
        return new PageImpl<>(responseList,pageable,communities.getTotalElements());
    }

//    //게시글 단건(상세조회)
//    public CommunityDetailResponse getCommunity(Long communityId) {
//    }
}
