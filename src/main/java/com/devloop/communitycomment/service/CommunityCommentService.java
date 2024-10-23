package com.devloop.communitycomment.service;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.community.entity.Community;
import com.devloop.community.repository.CommunityRepository;
import com.devloop.communitycomment.dto.CommentResponse;
import com.devloop.communitycomment.dto.request.CommentSaveRequest;
import com.devloop.communitycomment.dto.request.CommentUpdateRequest;
import com.devloop.communitycomment.dto.response.CommentSaveResponse;
import com.devloop.communitycomment.dto.response.CommentUpdateResponse;
import com.devloop.communitycomment.entity.CommunityComment;
import com.devloop.communitycomment.repository.CommunityCommentRepository;
import com.devloop.user.entity.User;
import com.devloop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityCommentService {
    private final CommunityCommentRepository communityCommentRepository;
    private final CommunityRepository communityRepository; //서비스 가져오는거로 바꿔야함
    private final UserRepository userRepository; //서비스에서 가져오게 바꿔야함

    //댓글 작성
    @Transactional
    public CommentSaveResponse creatComment(AuthUser authUser, CommentSaveRequest commentSaveRequest, Long communityId) {
        //커뮤니티 게시글 조회
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_COMMUNITY));
        //사용자 조회
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_USER));
        //댓글 생성..?생성자가 프라이빗이고..?
        CommunityComment communityComment = CommunityComment.from(commentSaveRequest, community, user);
        //댓글 저장
        CommunityComment savedComment = communityCommentRepository.save(communityComment);
        //응답으로 변환
        return CommentSaveResponse.from(savedComment);
    }

    //댓글 수정
    @Transactional
    public CommentUpdateResponse updateComment(AuthUser authUser, CommentUpdateRequest commentUpdateRequest, Long communityId, Long commentId) {
        //댓글 유무확인
        CommunityComment communityComment = communityCommentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_COMMENT));
        //댓글이 해당 게시글에 속해있는지 확인
        if (!communityComment.getCommunity().getId().equals(communityId)) {
            throw new ApiException(ErrorStatus._NOT_INCLUDE_COMMENT);
        }
        //권한 확인 : 댓글 작성자와 현재 사용자(authUser)가 같은지 확인
        if (!communityComment.getUser().getId().equals(authUser.getId())) {
            throw new ApiException(ErrorStatus._INVALID_COMMENTUSER);
        }
        //댓글내용 업데이트
        communityComment.updateContent(commentUpdateRequest.getContent());
        //업데이트 내용 저장
        CommunityComment updatedComment = communityCommentRepository.save(communityComment);
        //응답으로 반환
        return CommentUpdateResponse.from(updatedComment);
    }

    //댓글 삭제
    @Transactional
    public void deleteComment(AuthUser authUser, Long communityId, Long commentId) {
        //댓글 유무 확인
        CommunityComment communityComment = communityCommentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_COMMENT));
        //댓글이 해당 게시글에 속한건지 확인
        if (!communityComment.getCommunity().getId().equals(communityId)) {
            throw new ApiException(ErrorStatus._NOT_INCLUDE_COMMENT);
        }
        //댓글 작성자와 현재 사용자가 같은지 확인
        if (!communityComment.getUser().getId().equals(authUser.getId())) {
            throw new ApiException(ErrorStatus._INVALID_COMMENTUSER);
        }
        //삭제
        communityCommentRepository.delete(communityComment);
    }

    //댓글 조회
    public List<CommentResponse> getComments(Long communityId) {
        //게시글ID로 해당 게시글의 댓글 조회
        List<CommunityComment> comments = communityCommentRepository.findByCommunityId(communityId);

        List<CommentResponse> commentResponses = new ArrayList<>();
        //응답 dto로 바꿔주고 반환
        for (CommunityComment comment : comments) {
            CommentResponse commentResponse = CommentResponse.from(comment);
            commentResponses.add(commentResponse);
        }
        return commentResponses;

    }
}
