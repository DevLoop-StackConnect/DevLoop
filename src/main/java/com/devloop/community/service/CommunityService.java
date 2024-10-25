package com.devloop.community.service;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.enums.Category;
import com.devloop.common.exception.ApiException;
import com.devloop.community.dto.request.CommunitySaveRequest;
import com.devloop.community.dto.request.CommunityUpdateRequest;
import com.devloop.community.dto.response.CommunityDetailResponse;
import com.devloop.community.dto.response.CommunitySaveResponse;
import com.devloop.community.dto.response.CommunitySimpleResponse;
import com.devloop.community.entity.Community;
import com.devloop.community.entity.ResolveStatus;
import com.devloop.community.repository.CommunityRepository;
import com.devloop.user.entity.User;
import com.devloop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository; //서비스로 받아오게

    public Community getCommunityId(Long communityId){
        return communityRepository.findById(communityId)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_COMMUNITY));
    }

    //게시글 작성
    @Transactional
    public CommunitySaveResponse createCommunity(AuthUser authUser, CommunitySaveRequest communitySaveRequest) {
        Category category = Category.of(communitySaveRequest.getCategory());
        //사용자 조회
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_USER));
        //게시글 Community객체 생성
        Community community = Community.of(
                communitySaveRequest.getTitle(),
                communitySaveRequest.getContent(),
                category,
                user);
        //게시글 저장
        Community savedCommunity = communityRepository.save(community);
        //응답반환
        return CommunitySaveResponse.of(
                savedCommunity.getId(),
                savedCommunity.getTitle(),
                savedCommunity.getContent(),
                savedCommunity.getResolveStatus().getDescription(),
                savedCommunity.getCategory().getDescription(),
                savedCommunity.getCreatedAt()
        );
    }

    //게시글 다건 조회
    public Page<CommunitySimpleResponse> getCommunities(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        //페이지네이션된 게시글 조회
        return communityRepository.findAllSimple(pageable);
    }

    //게시글 단건(상세조회)
    public CommunityDetailResponse getCommunity(Long communityId) {
        //게시글 조회
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_COMMUNITY));

        //응답반환
        return CommunityDetailResponse.of(
                community.getId(),
                community.getTitle(),
                community.getContent(),
                community.getResolveStatus().getDescription(),
                community.getCategory().getDescription(),
                community.getCreatedAt(),
                community.getModifiedAt()
        );
    }

    //게시글 수정
    @Transactional
    public CommunityDetailResponse updateCommunity(Long communityId, CommunityUpdateRequest communityUpdateRequest) {
        ResolveStatus resolvedStatus = ResolveStatus.of(communityUpdateRequest.getStatus());
        Category category = Category.of(communityUpdateRequest.getCategory());
        //게시글 조회
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_COMMUNITY));

        //수정 요청에서 값이 있는 필드만 업데이트시키기
        community.updateCommunity(
                communityUpdateRequest.getTitle(),
                communityUpdateRequest.getContent(),
                resolvedStatus,
                category
        );
        //수정된 게시글 저장
        communityRepository.save(community);
        //응답반환
        return CommunityDetailResponse.of(
                community.getId(),
                community.getTitle(),
                community.getContent(),
                community.getResolveStatus().getDescription(),
                community.getCategory().getDescription(),
                community.getCreatedAt(),
                community.getModifiedAt()
        );
    }

    //게시글 삭제
    @Transactional
    public void deleteCommunity(Long communityId) {
        //게시글 존재하는지 확인
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_COMMUNITY));
        //삭제
        communityRepository.delete(community);
    }
}
