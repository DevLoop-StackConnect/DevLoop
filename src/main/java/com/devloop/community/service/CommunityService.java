package com.devloop.community.service;

import java.net.URL;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import com.devloop.common.AuthUser;
import com.devloop.user.entity.User;
import lombok.RequiredArgsConstructor;
import com.devloop.common.enums.Category;
import com.devloop.common.enums.BoardType;
import com.devloop.attachment.s3.S3Service;
import com.devloop.user.service.UserService;
import org.springframework.data.domain.Page;
import com.devloop.community.entity.Community;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import com.devloop.common.exception.ApiException;
import com.devloop.community.entity.ResolveStatus;
import com.devloop.common.utils.SearchResponseUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;
import com.devloop.common.apipayload.status.ErrorStatus;
import org.springframework.data.jpa.domain.Specification;
import com.devloop.attachment.entity.CommunityAttachment;
import com.devloop.community.request.CommunitySaveRequest;
import com.devloop.community.repository.CommunityRepository;
import com.devloop.community.request.CommunityUpdateRequest;
import com.devloop.community.response.CommunitySaveResponse;
import com.devloop.search.response.IntegrationSearchResponse;
import com.devloop.community.response.CommunityDetailResponse;
import com.devloop.community.response.CommunitySimpleResponse;
import org.springframework.transaction.annotation.Transactional;
import com.devloop.attachment.repository.CommunityATMRepository;
import com.devloop.attachment.service.CommunityAttachmentService;
import com.devloop.common.apipayload.dto.CommunitySimpleResponseDto;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final UserService userService;
    private final S3Service s3Service;
    private final CommunityATMRepository communityATMRepository;
    private final CommunityAttachmentService communityAttachmentService;

    //게시글 작성
    @Transactional
    public CommunitySaveResponse createCommunity(AuthUser authUser, MultipartFile file, CommunitySaveRequest communitySaveRequest) {
        Category category = communitySaveRequest.getCategory();
        //사용자 조회
        User user = userService.findByUserId(authUser.getId());
        //게시글 Community객체 생성
        Community community = Community.of(
                communitySaveRequest.getTitle(),
                communitySaveRequest.getContent(),
                category,
                user);
        //게시글 저장
        Community savedCommunity = communityRepository.save(community);
        //첨부파일 있으면 저장
        if (file != null && !file.isEmpty()) {
            s3Service.uploadFile(file, user, community); //s3에 파일 올리고 communityattachment에 저장하는 것
        }
        //응답반환
        return CommunitySaveResponse.of(
                savedCommunity.getId(),
                savedCommunity.getTitle(),
                savedCommunity.getContent(),
                savedCommunity.getResolveStatus().getDescription(),
                category.getDescription(),
                savedCommunity.getCreatedAt()
        );
    }

    //게시글 다건 조회
    public Page<CommunitySimpleResponse> getCommunities(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        //페이지네이션된 게시글 조회하고 응답
        Page<CommunitySimpleResponseDto> communityDtos = communityRepository.findAllSimple(pageable);

        if (communityDtos.isEmpty()) {
            throw new ApiException(ErrorStatus._NOT_FOUND_COMMUNITY);
        }

        return communityDtos.map(dto -> CommunitySimpleResponse.of(
                dto.getCommunityId(),
                dto.getTitle(),
                dto.getStatus().getDescription(),
                dto.getCategory().getDescription()
        ));
    }

    //게시글 단건(상세조회)
    public CommunityDetailResponse getCommunity(Long communityId) {
        //게시글 조회
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_COMMUNITY));
        //첨부파일 url이 있는지 확인
        String imageUrl = communityATMRepository.findByCommunityId(communityId) //첨부파일 있는지 조회
                .map(CommunityAttachment::getImageURL)
                .map(URL::toString)
                .orElse(null);
        //응답반환
        return CommunityDetailResponse.withAttachment(
                community.getId(),
                community.getTitle(),
                community.getContent(),
                community.getResolveStatus().getDescription(),
                community.getCategory().getDescription(),
                community.getCreatedAt(),
                community.getModifiedAt(),
                imageUrl
        );
    }

    //게시글 수정
    @Transactional
    public CommunityDetailResponse updateCommunity(AuthUser authUser, Long communityId, CommunityUpdateRequest communityUpdateRequest, MultipartFile file) {
        ResolveStatus status = communityUpdateRequest.getStatus();

        if (status == ResolveStatus.SOLVED) {
            log.info("게시글 상태가 해결된 상태로 변경되었습니다.");
        } else {
            log.info("게시글이 미해결 상태로 전환되었습니다.");
        }

        Category category = communityUpdateRequest.getCategory();

        //게시글 조회
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_COMMUNITY));

        //작성자 확인
        if (!community.getUser().getId().equals(authUser.getId())) {
            throw new ApiException(ErrorStatus._PERMISSION_DENIED);
        }

        //수정 요청에서 값이 있는 필드만 업데이트시키기
        community.updateCommunity(
                communityUpdateRequest.getTitle(),
                communityUpdateRequest.getContent(),
                status,
                category
        );

        //첨부파일 수정
        if (file != null && !file.isEmpty()) {
            // 기존 파일이 있는지 확인
            CommunityAttachment communityAttachment = communityAttachmentService.findCommunityAttachmentByCommunityId(communityId);
            if (communityAttachment == null) {
                s3Service.uploadFile(file, community.getUser(), community);
            } else {
                s3Service.updateUploadFile(file, communityAttachment, community);
            }
        }
        //응답반환
        return CommunityDetailResponse.withoutAttachment(
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
    public void deleteCommunity(AuthUser authUser, Long communityId) {
        //게시글 존재하는지 확인
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_COMMUNITY));
        //작성자 확인
        if (!community.getUser().getId().equals(authUser.getId())) {
            throw new ApiException(ErrorStatus._PERMISSION_DENIED);
        }
        // 첨부파일 확인 및 삭제
        communityATMRepository.findByCommunityId(communityId).ifPresent(attachment -> {
            s3Service.delete(attachment.getFileName());
            communityATMRepository.delete(attachment);
        });
        //삭제
        communityRepository.delete(community);
    }

    //Util
    public Community getCommunityId(Long communityId) {
        return communityRepository.findById(communityId)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_COMMUNITY));
    }

    /**
     * Search에서 사용
     */
    public List<IntegrationSearchResponse> getAllCommunity(Specification<Community> spec) {
        List<Community> communities = communityRepository.findAll(spec);
        return SearchResponseUtil.wrapResponse(BoardType.COMMUNITY, communities);
    }

    public Page<IntegrationSearchResponse> getCommunityWithPage(Specification<Community> spec, PageRequest pageable) {
        Page<Community> communityPage = communityRepository.findAll(spec, pageable);
        List<IntegrationSearchResponse> response = SearchResponseUtil.wrapResponse(
                BoardType.COMMUNITY,
                communityPage.getContent()
        );
        return new PageImpl<>(response, pageable, communityPage.getTotalElements());
    }
}
