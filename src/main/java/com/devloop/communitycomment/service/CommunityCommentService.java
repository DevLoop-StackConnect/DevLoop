package com.devloop.communitycomment.service;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.common.utils.NotificationHandler;
import com.devloop.community.entity.Community;
import com.devloop.community.service.CommunityService;
import com.devloop.communitycomment.dto.CommentResponse;
import com.devloop.communitycomment.dto.request.CommentSaveRequest;
import com.devloop.communitycomment.dto.request.CommentUpdateRequest;
import com.devloop.communitycomment.dto.response.CommentSaveResponse;
import com.devloop.communitycomment.dto.response.CommentUpdateResponse;
import com.devloop.communitycomment.entity.CommunityComment;
import com.devloop.communitycomment.repository.CommunityCommentRepository;
import com.devloop.notification.dto.NotificationMessage;
import com.devloop.notification.enums.NotificationType;
import com.devloop.party.entity.Party;
import com.devloop.partycomment.entity.PartyComment;
import com.devloop.user.entity.User;
import com.devloop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CommunityCommentService {
    private final CommunityCommentRepository communityCommentRepository;
    private final CommunityService communityService;
    private final UserRepository userRepository; //서비스에서 가져오게 바꿔야함
    private final NotificationHandler notificationHandler;

    //댓글 작성
    @Transactional
    public CommentSaveResponse creatComment(AuthUser authUser, CommentSaveRequest commentSaveRequest, Long communityId) {
        try {
            //커뮤니티 게시글 조회
            Community community = communityService.getCommunityId(communityId);

            //사용자 조회
            User user = userRepository.findById(authUser.getId())
                    .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_USER));

            //게시글 작성자 가져오기
            User postAuthor = community.getUser();

            //댓글 생성..?생성자가 프라이빗이고..?
            CommunityComment communityComment = CommunityComment.of(commentSaveRequest.getContent(), community, user);
            //댓글 저장
            CommunityComment savedComment = communityCommentRepository.save(communityComment);
            //응답으로 변환

            //알림 전송 - 작성자가 댓글 작성자와 다른 경우만 전송
            if (!Objects.equals(user.getId(), postAuthor.getId())) {
                notifyNewComment(communityComment);
            }

            return CommentSaveResponse.of(savedComment.getId(), savedComment.getContent(), savedComment.getCreatedAt());
        } catch (Exception e) {
            notifyErrorCreation(communityId, authUser.getId(), e.getMessage());
            throw e;
        }
    }

    //댓글 수정
    @Transactional
    public CommentUpdateResponse updateComment(AuthUser authUser, CommentUpdateRequest commentUpdateRequest, Long communityId, Long commentId) {
        try {//댓글 유무확인
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
            return CommentUpdateResponse.of(updatedComment.getId(), updatedComment.getContent(), updatedComment.getModifiedAt());
        } catch(Exception e){
            notifyErrorCommentUpdate(communityId, commentId, authUser.getId(), e.getMessage());
            throw e;
        }
    }

    //댓글 삭제
    @Transactional
    public void deleteComment(AuthUser authUser, Long communityId, Long commentId) {
        try {
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
        } catch (Exception e){
            notifyErrorCommentDeletion(communityId,commentId, authUser.getId(), e.getMessage());
            throw e;
        }
    }

    //댓글 다건 조회
    public Page<CommentResponse> getComments(Long communityId, Pageable pageable) {
        //페이지네이션된 댓글 조회
        Page<CommunityComment> comments = communityCommentRepository.findByCommunityId(communityId, pageable);

        List<CommentResponse> commentResponses = new ArrayList<>();
        //응답반환
        for (CommunityComment comment : comments.getContent()) {
            CommentResponse commentResponse = CommentResponse.of(comment.getId(), comment.getContent(), comment.getUser().getUsername(), comment.getCreatedAt());
            commentResponses.add(commentResponse);
        }
        return new PageImpl<>(commentResponses, comments.getPageable(), comments.getTotalElements());
    }

    //새 댓글 알림 전송 메서드
    private void notifyNewComment(CommunityComment communityComment) {
        try {
            User postAuthor = communityComment.getCommunity().getUser();
            User commentAuthor = communityComment.getUser();
            Community community = communityComment.getCommunity();

            String targetSlackId = postAuthor.getSlackId();
            if (targetSlackId == null || targetSlackId.isEmpty()) {
                log.debug("Slack 연동되지 않은 사용자 : {}", postAuthor.getId());
                return;
            }
            //알림 메시지 생성
            NotificationMessage message = NotificationMessage.builder()
                    .type(NotificationType.COMMUNITY_COMMENT)
                    .notificationTarget("@" + targetSlackId)
                    .data(Map.of(
                            "postTitle", community.getTitle(),
                            "commentAuthor", commentAuthor.getUsername(),
                            "content", communityComment.getContent(),
                            "category", community.getCategory().getDescription(),
                            "userId", targetSlackId,
                            "postId", community.getId().toString(),
                            "commentId", communityComment.getId().toString()
                    ))
                    .timestamp(LocalDateTime.now())
                    .build();
            notificationHandler.sendNotification(message);
            log.debug("댓글 알림 전송 완료 - 게시글 : {}", community.getId(), communityComment.getId());
        } catch (Exception e) {
            log.warn("댓글 알림 전송 실패 - 댓글 ID : {}, 사유 - {}", communityComment.getId(), e.getMessage());
        }
    }

    public void notifyErrorCreation(Long communityId, Long userId, String errorMessage) {
        log.error("커뮤니티 댓글 생성 실패 - communityId : {}, userId : {}, error : {}", communityId, userId, errorMessage);
    }

    public void notifyErrorCommentUpdate(Long communityId, Long commentId, Long userId, String errorMessage) {
        log.error("커뮤니티 댓글 수정 실패  - communityId : {}, commentId : {}, userId : {}, error : {}", communityId, commentId, userId, errorMessage);
    }

    public void notifyErrorCommentDeletion(Long communityId, Long commentId, Long userId, String errorMessage) {
        log.error("커뮤니티 댓글 삭제 실패  - communityId : {}, commentId : {}, userId : {}, error : {}", communityId, commentId, userId, errorMessage);
    }
}
