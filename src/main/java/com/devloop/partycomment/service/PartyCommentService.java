package com.devloop.partycomment.service;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.party.entity.Party;
import com.devloop.party.repository.PartyRepository;
import com.devloop.partycomment.entity.PartyComment;
import com.devloop.partycomment.repository.PartyCommentRepository;
import com.devloop.partycomment.request.SavePartyCommentRequest;
import com.devloop.partycomment.request.UpdatePartyCommentRequest;
import com.devloop.partycomment.response.GetPartyCommentListResponse;
import com.devloop.partycomment.response.SavePartyCommentResponse;
import com.devloop.partycomment.response.UpdatePartyCommentResponse;
import com.devloop.user.entity.User;
import com.devloop.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartyCommentService {
    private final PartyCommentRepository partyCommentRepository;
    private final UserRepository userRepository;
    private final PartyRepository partyRepository;

    //스터디 파티 게시글 댓글 등록
    @Transactional
    public SavePartyCommentResponse savePartyComment(AuthUser authUser, Long partyId, SavePartyCommentRequest savePartyCommentRequest) {
        //유저가 존재하는 지 확인
        User user=userRepository.findById(authUser.getId()).orElseThrow(()->
            new ApiException(ErrorStatus._NOT_FOUND_USER));

        //스터디 파티 게시글이 존재하는 지 확인
        Party party=partyRepository.findById(partyId).orElseThrow(()->
            new ApiException(ErrorStatus._NOT_FOUND_PARTY));

        //새로운 댓글 생성 및 저장
        PartyComment newPartyComment=PartyComment.from(savePartyCommentRequest,user,party);
        partyCommentRepository.save(newPartyComment);

        return SavePartyCommentResponse.from(party.getId(),newPartyComment);
    }

    //스터디 파티 게시글 댓글 수정
    @Transactional
    public UpdatePartyCommentResponse updatePartyComment(AuthUser authUser, Long partyId, Long commentId, UpdatePartyCommentRequest updatePartyCommentRequest) {
        //스터디 파티 게시글이 존재하는 지 확인
        Party party=partyRepository.findById(partyId).orElseThrow(()->
                new ApiException(ErrorStatus._NOT_FOUND_PARTY));

        //댓글이 존재하는 지 확인
        PartyComment partyComment=partyCommentRepository.findById(commentId).orElseThrow(()->
                new ApiException(ErrorStatus._NOT_FOUND_COMMENT));

        //댓글을 작성한 유저가 맞는 지 확인
        if(!authUser.getId().equals(partyComment.getUser().getId())){
            throw new ApiException(ErrorStatus._PERMISSION_DENIED);
        }

        partyComment.update(updatePartyCommentRequest);

        return UpdatePartyCommentResponse.from(partyId,partyComment);
    }

    //스터디 파티 게시글 댓글 다건 조회
    public Page<GetPartyCommentListResponse> getPartyCommentList(Long partyId,int page,int size) {
        Pageable pageable= PageRequest.of(page-1,size);

        //스터디 파티 게시글이 존재하는 지 확인
        Party party=partyRepository.findById(partyId).orElseThrow(()->
                new ApiException(ErrorStatus._NOT_FOUND_PARTY));

        Page<PartyComment> partyComments=partyCommentRepository.findByParty(party,pageable);

        //댓글 리스트 조회
        return partyComments.map(partyComment->{
            return GetPartyCommentListResponse.from(partyComment);
        });
    }

    //스터디 파티 게시글 댓글 삭제
    @Transactional
    public void deletePartyComment(AuthUser authUser, Long partyId, Long commentId) {
        //스터디 파티 게시글이 존재하는 지 확인
        Party party=partyRepository.findById(partyId).orElseThrow(()->
                new ApiException(ErrorStatus._NOT_FOUND_PARTY));

        //댓글이 존재하는 지 확인
        PartyComment partyComment=partyCommentRepository.findById(commentId).orElseThrow(()->
                new ApiException(ErrorStatus._NOT_FOUND_COMMENT));

        //댓글을 작성한 유저가 맞는 지 확인
        if(!authUser.getId().equals(partyComment.getUser().getId())){
            throw new ApiException(ErrorStatus._PERMISSION_DENIED);
        }

        partyCommentRepository.delete(partyComment);
    }
}
