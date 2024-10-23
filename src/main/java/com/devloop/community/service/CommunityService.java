package com.devloop.community.service;

import com.devloop.common.AuthUser;
import com.devloop.community.dto.request.CommunitySaveRequest;
import com.devloop.community.dto.request.CommunityUpdateRequest;
import com.devloop.community.dto.response.CommunityDetailResponse;
import com.devloop.community.dto.response.CommunitySaveResponse;
import com.devloop.community.dto.response.CommunitySimpleResponse;
import com.devloop.community.entity.Community;
import com.devloop.community.repository.CommunityRepository;
import com.devloop.communitycomment.dto.CommentResponse;
import com.devloop.communitycomment.entity.CommunityComment;
import com.devloop.communitycomment.repository.CommunityCommentRepository;
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
    private final CommunityCommentRepository communityCommentRepository;

    //게시글 작성
    @Transactional
    public CommunitySaveResponse createCommunity(AuthUser authUser, CommunitySaveRequest communitySaveRequest) {
        System.out.println(communitySaveRequest.getStatus());
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

    //게시글 단건(상세조회)
    public CommunityDetailResponse getCommunity(Long communityId) {
        //게시글 조회
        Community community = communityRepository.findById(communityId)
                .orElseThrow(()->new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));
        //댓글 조회
        List<CommunityComment> comments = communityCommentRepository.findByCommunityId(communityId);

        List<CommentResponse> commentResponses = new ArrayList<>();
        for (CommunityComment comment : comments){
            commentResponses.add(new CommentResponse(
                    comment.getId(),
                    comment.getContent(),
                    comment.getUser().getUsername(),
                    comment.getCreatedAt(),
                    comment.getModifiedAt()
            ));
        }
        //게시글,댓글 정보 응답반환
        return new CommunityDetailResponse(
                community.getId(),
                community.getTitle(),
                community.getContent(),
                community.getCreatedAt(),
                community.getModifiedAt(),
                community.getResolveStatus(),
                community.getCategory(),
                commentResponses
        );
    }

    //게시글 수정
    @Transactional
    public CommunityDetailResponse updateCommunity(Long communityId, CommunityUpdateRequest communityUpdateRequest) {
        //게시글 조회
        Community community = communityRepository.findById(communityId)
                .orElseThrow(()->new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        //수정 요청에서 값이 있는 필드만 업데이트시키기
        community.updateCommunity(
                communityUpdateRequest.getTitle(),
                communityUpdateRequest.getContent(),
                communityUpdateRequest.getStatus(),
                communityUpdateRequest.getCategory()
        );
        //수정된 게시글 저장
        communityRepository.save(community);
        //응답반환
        return new CommunityDetailResponse(
                community.getId(),
                community.getTitle(),
                community.getContent(),
                community.getCreatedAt(),
                community.getModifiedAt(),
                community.getResolveStatus(),
                community.getCategory(),
                new ArrayList<>()
        );
    }
}
