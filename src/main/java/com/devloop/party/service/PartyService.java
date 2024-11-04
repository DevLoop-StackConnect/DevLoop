package com.devloop.party.service;

import com.devloop.attachment.entity.PartyAttachment;
import com.devloop.attachment.s3.S3Service;
import com.devloop.attachment.service.PartyAttachmentService;
import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.enums.BoardType;
import com.devloop.common.exception.ApiException;
import com.devloop.common.utils.SearchResponseUtil;
import com.devloop.party.entity.Party;
import com.devloop.party.repository.PartyRepository;
import com.devloop.party.request.SavePartyRequest;
import com.devloop.party.request.UpdatePartyRequest;
import com.devloop.party.response.GetPartyDetailResponse;
import com.devloop.party.response.GetPartyListResponse;
import com.devloop.party.response.SavePartyResponse;
import com.devloop.party.response.UpdatePartyResponse;
import com.devloop.search.response.IntegrationSearchResponse;
import com.devloop.user.entity.User;
import com.devloop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PartyService {
    private final PartyRepository partyRepository;
    private final UserService userService;
    private final S3Service s3Service;
    private final PartyAttachmentService partyAttachmentService;

    //스터디 파티 모집 게시글 등록
    @Transactional
    public SavePartyResponse saveParty(AuthUser authUser, MultipartFile file,SavePartyRequest savePartyRequest) {
        //유저가 존재하는 지 확인
        User user=userService.findByUserId(authUser.getId());

        //새로운 파티 생성
        Party newParty=Party.from(savePartyRequest, user);
        partyRepository.save(newParty);

        //파일이 있을 때만 업로드
        if(file!=null && !file.isEmpty()){
            s3Service.uploadFile(file,user,newParty);
        }

        return SavePartyResponse.of(
                newParty.getId(),
                newParty.getTitle(),
                newParty.getContents(),
                newParty.getStatus().getStatus(),
                newParty.getCategory().getDescription()
        );
    }

    //스터디 파티 모집 게시글 수정
    @Transactional
    public UpdatePartyResponse updateParty(AuthUser authUser, Long partyId, MultipartFile file,UpdatePartyRequest updatePartyRequest) {
        //유저가 존재하는 지 확인
        User user=userService.findByUserId(authUser.getId());

        //게시글이 존재하는 지 확인
        Party party=partyRepository.findById(partyId).orElseThrow(()->
                new ApiException(ErrorStatus._NOT_FOUND_PARTY));

        //게시글을 작성한 유저가 맞는 지 확인
        if(!authUser.getId().equals(party.getUser().getId())){
            throw new ApiException(ErrorStatus._PERMISSION_DENIED);
        }

        /**
         * 추가된 파일 이 있는 지 확인
         * -> 기존 파일이 있는 지 확인
         * 파일이 있으면 삭제 후 업로드 , 없으면 바로 업로드
         */
        //추가된 파일이 있는지 확인
        if(file!=null && !file.isEmpty()){
            Optional<PartyAttachment> partyAttachment=partyAttachmentService.findPartyAttachmentByPartyId(partyId);

            //기존 파일이 있는지 확인
            if(partyAttachment.isEmpty()){
                s3Service.uploadFile(file,user,party);
            }else{
                s3Service.updateUploadFile(file,partyAttachment.get(),party);
            }

        }

        party.update(updatePartyRequest);

        return UpdatePartyResponse.of(
                party.getId(),
                party.getTitle(),
                party.getContents(),
                party.getStatus().getStatus(),
                party.getCategory().getDescription()
        );
    }

    //스터디 파티 모집 게시글 단건 조회
    public GetPartyDetailResponse getParty(Long partyId) {
        //게시글이 존재하는 지 확인
        Party party=partyRepository.findById(partyId).orElseThrow(()->
                new ApiException(ErrorStatus._NOT_FOUND_PARTY));

        //기존 파일이 있는 지 확인
        Optional<PartyAttachment> partyAttachment=partyAttachmentService.findPartyAttachmentByPartyId(partyId);


        return GetPartyDetailResponse.of(
                party.getId(),
                party.getTitle(),
                party.getContents(),
                party.getStatus().getStatus(),
                party.getCategory().getDescription(),
                party.getCreatedAt(),
                party.getModifiedAt(),
                String.valueOf(partyAttachment.get().getImageURL())
        );
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
        return parties.map(party->GetPartyListResponse.of(
                    party.getId(),
                    party.getTitle(),
                    party.getContents(),
                    party.getStatus().getStatus(),
                    party.getCategory().getDescription()));
    }

    //스터디 파티 모집 게시글 삭제
    @Transactional
    public String deleteParty(AuthUser authUser, Long partyId) {
        //게시글이 존재하는 지 확인
        Party party=partyRepository.findById(partyId).orElseThrow(()->
                new ApiException(ErrorStatus._NOT_FOUND_PARTY));

        //게시글을 작성한 유저가 맞는 지 확인
        if(!authUser.getId().equals(party.getUser().getId())){
            throw new ApiException(ErrorStatus._PERMISSION_DENIED);
        }

        Optional<PartyAttachment> partyAttachment=partyAttachmentService.findPartyAttachmentByPartyId(partyId);
        //파일이 있는지 확인
        if(partyAttachment.isPresent()){
            //파일 삭제 (S3, 로컬)
            s3Service.delete(partyAttachment.get().getFileName());
            partyAttachmentService.deletePartyAttachment(partyAttachment.get());
        }
        partyRepository.delete(party);

        return String.format("%s 게시글을 삭제하였습니다.", party.getTitle());
    }


    /**
     * Search에서 사용
     */
    public List<IntegrationSearchResponse> getParty(Specification<Party> spec) {
        List<Party> parties = partyRepository.findAll(spec);
        return SearchResponseUtil.wrapResponse(BoardType.PARTY, parties);
    }

    public Page<IntegrationSearchResponse> getPartyWithPage(Specification<Party> spec, PageRequest pageable) {
        Page<Party> partyPage = partyRepository.findAll(spec, pageable);
        List<IntegrationSearchResponse> response = SearchResponseUtil.wrapResponse(
                BoardType.PARTY,
                partyPage.getContent()
        );
        return new PageImpl<>(response, pageable, partyPage.getTotalElements());
    }

    //스터디 파티 id로 조회
    public Party findById(Long id){
        return partyRepository.findById(id).orElseThrow(()->
                new ApiException(ErrorStatus._NOT_FOUND_PARTY));
    }
}
