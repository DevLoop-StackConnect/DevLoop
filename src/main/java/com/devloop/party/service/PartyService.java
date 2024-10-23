package com.devloop.party.service;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.common.utils.S3Util;
import com.devloop.party.entity.Party;
import com.devloop.party.repository.PartyRepository;
import com.devloop.party.request.SavePartyRequest;
import com.devloop.party.request.UpdatePartyRequest;
import com.devloop.party.response.GetPartyDetailResponse;
import com.devloop.party.response.GetPartyListResponse;
import com.devloop.party.response.SavePartyResponse;
import com.devloop.party.response.UpdatePartyResponse;
import com.devloop.user.entity.User;
import com.devloop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartyService {

    private final PartyRepository partyRepository;
    private final UserRepository userRepository;
    private final S3Util s3Util;

    //스터디 파티 모집 게시글 등록
    @Transactional
    public SavePartyResponse saveParty(AuthUser authUser, MultipartFile file,SavePartyRequest savePartyRequest) {
        //유저가 존재하는 지 확인
        User user=userRepository.findById(authUser.getId()).orElseThrow(()->
                new ApiException(ErrorStatus._NOT_FOUND_USER));

        //새로운 파티 생성
        Party newParty=Party.from(savePartyRequest, user);
        partyRepository.save(newParty);

//        //파일 업로드
//        s3Util.uploadFile(file);

        return SavePartyResponse.from(newParty);
    }

    //스터디 파티 모집 게시글 수정
    @Transactional
    public UpdatePartyResponse updateParty(AuthUser authUser, Long partyId, UpdatePartyRequest updatePartyRequest) {
        //게시글이 존재하는 지 확인
        Party party=partyRepository.findById(partyId).orElseThrow(()->
                new ApiException(ErrorStatus._NOT_FOUND_PARTY));

        //게시글을 작성한 유저가 맞는 지 확인
        if(!authUser.getId().equals(party.getUser().getId())){
            throw new ApiException(ErrorStatus._PERMISSION_DENIED);
        }
        party.update(updatePartyRequest);
        return UpdatePartyResponse.from(party);
    }

    //스터디 파티 모집 게시글 단건 조회
    public GetPartyDetailResponse getParty(Long partyId) {
        //게시글이 존재하는 지 확인
        Party party=partyRepository.findById(partyId).orElseThrow(()->
                new ApiException(ErrorStatus._NOT_FOUND_PARTY));
        return GetPartyDetailResponse.from(party);
    }

    //스터디 파티 모집 게시글 다건 조회
    public Page<GetPartyListResponse> getPartyList(String title,int page, int size) {
        PageRequest pageable= PageRequest.of(page-1,size);

        Page<Party> parties;

        if(title==null || title.isEmpty()){
            parties=partyRepository.findAll(pageable);
        }else{
            parties=partyRepository.findByTitleContaining(title,pageable);
        }
        return parties.map(party->{
            return GetPartyListResponse.from(party);
        });
    }

    //스터디 파티 모집 게시글 삭제
    @Transactional
    public void deleteParty(AuthUser authUser, Long partyId) {
        //게시글이 존재하는 지 확인
        Party party=partyRepository.findById(partyId).orElseThrow(()->
                new ApiException(ErrorStatus._NOT_FOUND_PARTY));

        //게시글을 작성한 유저가 맞는 지 확인
        if(!authUser.getId().equals(party.getUser().getId())){
            throw new ApiException(ErrorStatus._PERMISSION_DENIED);
        }
        partyRepository.delete(party);
    }
}
