package com.devloop.partycomment.service;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.common.utils.NotificationHandler;
import com.devloop.notification.dto.NotificationMessage;
import com.devloop.notification.enums.NotificationType;
import com.devloop.party.entity.Party;
import com.devloop.party.service.PartyService;
import com.devloop.partycomment.entity.PartyComment;
import com.devloop.partycomment.repository.PartyCommentRepository;
import com.devloop.partycomment.request.SavePartyCommentRequest;
import com.devloop.partycomment.request.UpdatePartyCommentRequest;
import com.devloop.partycomment.response.GetPartyCommentListResponse;
import com.devloop.partycomment.response.SavePartyCommentResponse;
import com.devloop.partycomment.response.UpdatePartyCommentResponse;
import com.devloop.user.entity.User;
import com.devloop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartyCommentService {
    private final PartyCommentRepository partyCommentRepository;
    private final UserService userService;
    private final PartyService partyService;
    private final NotificationHandler notificationHandler;

    //스터디 파티 게시글 댓글 등록
    @Transactional
    public SavePartyCommentResponse savePartyComment(AuthUser authUser, Long partyId, SavePartyCommentRequest savePartyCommentRequest) {
        try {
            //유저가 존재하는 지 확인
            User user = userService.findByUserId(authUser.getId());

            //스터디 파티 게시글이 존재하는 지 확인
            Party party = partyService.findById(partyId);

            User postAuthor = party.getUser();

            //새로운 댓글 생성 및 저장
            PartyComment newPartyComment = PartyComment.from(savePartyCommentRequest, user, party);
            partyCommentRepository.save(newPartyComment);

            if (!Objects.equals(user.getId(), postAuthor.getId())) {
                notifyNewComment(newPartyComment);
            }

            return SavePartyCommentResponse.of(
                    partyId,
                    newPartyComment.getId(),
                    newPartyComment.getComment()
            );
        } catch (Exception e) {
            notifyErrorCreatioin(partyId, authUser.getId(), e.getMessage());
            throw e;
        }
    }

    //스터디 파티 게시글 댓글 수정
    @Transactional
    public UpdatePartyCommentResponse updatePartyComment(AuthUser authUser, Long partyId, Long commentId, UpdatePartyCommentRequest updatePartyCommentRequest) {
        try {
            //스터디 파티 게시글이 존재하는 지 확인
            Party party = partyService.findById(partyId);

            //댓글이 존재하는 지 확인
            PartyComment partyComment = partyCommentRepository.findById(commentId).orElseThrow(() ->
                    new ApiException(ErrorStatus._NOT_FOUND_COMMENT));

            //댓글을 작성한 유저가 맞는 지 확인
            if (!authUser.getId().equals(partyComment.getUser().getId())) {
                throw new ApiException(ErrorStatus._PERMISSION_DENIED);
            }

            partyComment.update(updatePartyCommentRequest);

            return UpdatePartyCommentResponse.of(
                    partyId,
                    partyComment.getId(),
                    partyComment.getComment()
            );
        } catch (Exception e) {
            notifyErrorCommentUpdate(partyId, commentId, authUser.getId(), e.getMessage());
            throw e;
        }
    }

    //스터디 파티 게시글 댓글 다건 조회
    public Page<GetPartyCommentListResponse> getPartyCommentList(Long partyId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        //스터디 파티 게시글이 존재하는 지 확인
        Party party = partyService.findById(partyId);

        Page<PartyComment> partyComments = partyCommentRepository.findByPartyId(party.getId(), pageable);

        //댓글 리스트 조회
        return partyComments.map(partyComment -> {
            return GetPartyCommentListResponse.of(
                    partyComment.getUser().getUsername(),
                    partyComment.getId(),
                    partyComment.getComment());
        });
    }

    //스터디 파티 게시글 댓글 삭제
    @Transactional
    public void deletePartyComment(AuthUser authUser, Long partyId, Long commentId) {
        try {
            //스터디 파티 게시글이 존재하는 지 확인
            Party party = partyService.findById(partyId);

            //댓글이 존재하는 지 확인
            PartyComment partyComment = partyCommentRepository.findById(commentId).orElseThrow(() ->
                    new ApiException(ErrorStatus._NOT_FOUND_COMMENT));

            boolean isAdmin =  authUser.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

            //댓글을 작성한 유저가 맞는 지 확인
            if (!authUser.getId().equals(partyComment.getUser().getId()) && !isAdmin) {
                throw new ApiException(ErrorStatus._PERMISSION_DENIED);
            }

            partyCommentRepository.delete(partyComment);

        } catch (Exception e) {
            notifyErrorCommentDeletion(partyId, commentId, authUser.getId(), e.getMessage());
            throw e;
        }
    }

    private void notifyNewComment(PartyComment partyComment) {
        try {
            User postAuthor = partyComment.getParty().getUser();
            User commentAuthor = partyComment.getUser();
            Party party = partyComment.getParty();

            String targetSlackId = postAuthor.getSlackId();
            if (targetSlackId == null || targetSlackId.isEmpty()) {
                log.debug("Slack 연동되지 않은 사용자 : {}", postAuthor.getId());
                return;
            }
            NotificationMessage message = NotificationMessage.builder()
                    .type(NotificationType.PARTY_COMMENT)
                    .notificationTarget("@" + targetSlackId)
                    .data(Map.of(
                            "postTitle", party.getTitle(),
                            "commentAuthor", commentAuthor.getUsername(),
                            "content", partyComment.getComment(),
                            "category", party.getCategory().getDescription(),
                            "userId", targetSlackId,
                            "postId", party.getId().toString(),
                            "commentId", partyComment.getId().toString()
                    ))
                    .timestamp(LocalDateTime.now())
                    .build();
            notificationHandler.sendNotification(message);
            log.debug("댓글 알림 전송 완료 - 게시글 : {}", party.getId(), partyComment.getId());
        } catch (Exception e) {
            log.warn("댓글 알림 전송 실패 - 댓글 ID : {}, 사유 - {}", partyComment.getId(), e.getMessage());
        }
    }

    public void notifyErrorCreatioin(Long partyId, Long userId, String errorMessage) {
        log.error("스터디 파티 모집 게시글 댓글 생성 실패 - partyId : {}, userId : {}, error : {}", partyId, userId, errorMessage);
    }

    public void notifyErrorCommentUpdate(Long partyId, Long commentId, Long userId, String errorMessage) {
        log.error("스터디 파티 모집 댓글 수정 실패 - partyId : {}, commentId : {}, userId : {}, error : {}", partyId, commentId, userId, errorMessage);
    }

    public void notifyErrorCommentDeletion(Long partyId, Long commentId, Long userId, String errorMessage) {
        log.error("스터디 파티 댓글 삭제 실패  - communityId : {}, commentId : {}, userId : {}, error : {}", partyId, commentId, userId, errorMessage);
    }
}
