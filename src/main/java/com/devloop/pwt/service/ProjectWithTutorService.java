package com.devloop.pwt.service;

import com.devloop.attachment.entity.PWTAttachment;
import com.devloop.attachment.s3.S3Service;
import com.devloop.attachment.service.PWTAttachmentService;
import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.dto.ProjectWithTutorResponseDto;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.enums.Approval;
import com.devloop.common.enums.Category;
import com.devloop.common.enums.BoardType;
import com.devloop.common.exception.ApiException;
import com.devloop.common.utils.SearchResponseUtil;
import com.devloop.pwt.entity.ProjectWithTutor;
import com.devloop.pwt.enums.Level;
import com.devloop.pwt.repository.ProjectWithTutorRepository;
import com.devloop.pwt.request.ProjectWithTutorSaveRequest;
import com.devloop.pwt.request.ProjectWithTutorUpdateRequest;
import com.devloop.pwt.response.ProjectWithTutorDetailResponse;
import com.devloop.pwt.response.ProjectWithTutorListResponse;
import com.devloop.search.response.IntegrationSearchResponse;
import com.devloop.user.entity.User;
import com.devloop.user.enums.UserRole;
import com.devloop.user.repository.UserRepository;
import com.devloop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectWithTutorService {

    private final ProjectWithTutorRepository projectWithTutorRepository;
    private final UserService userService;
    private final S3Service s3Service;
    private final PWTAttachmentService pwtAttachmentService;

    // 튜터랑 함께하는 협업 프로젝트 게시글 생성
    @Transactional
    public String saveProjectWithTutor(
            AuthUser authUser,
            MultipartFile file,
            ProjectWithTutorSaveRequest projectWithTutorSaveRequest
    ) {
        // 사용자 객체 가져오기
        User user = userService.findByUserId(authUser.getId());

        // 요청한 사용자의 권한이 USER일 경우 예외 처리
        if (user.getUserRole().equals(UserRole.ROLE_USER)) {
            throw new ApiException(ErrorStatus._HAS_NOT_ACCESS_PERMISSION);
        }

        // PWT 게시글 객체 생성
        ProjectWithTutor projectWithTutor = ProjectWithTutor.of(
                projectWithTutorSaveRequest.getTitle(),
                projectWithTutorSaveRequest.getDescription(),
                projectWithTutorSaveRequest.getPrice(),
                projectWithTutorSaveRequest.getDeadline(),
                projectWithTutorSaveRequest.getMaxParticipants(),
                Level.of(projectWithTutorSaveRequest.getLevel()),
                Category.of(projectWithTutorSaveRequest.getCategory()),
                user
        );
        projectWithTutorRepository.save(projectWithTutor);

        // 첨부파일 저장
        s3Service.uploadFile(file, user, projectWithTutor);

        return String.format("%s 님의 튜터랑 함께하는 협업 프로젝트 게시글이 작성 완료되었습니다. 승인까지 3~5일 정도 소요될 수 있습니다.", user.getUsername());
    }

    // 튜터랑 함께하는 협업 프로젝트 게시글 단건 조회 (승인이 완료된 게시글 단건 조회)
    public ProjectWithTutorDetailResponse getProjectWithTutor(Long projectId) {
        // PWT 게시글 객체 가져오기
        ProjectWithTutor projectWithTutor = projectWithTutorRepository.findById(projectId)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_PROJECT_WITH_TUTOR));

        // PWT 게시글 첨부파일 객체 가져오기
        PWTAttachment pwtAttachment = pwtAttachmentService.findPwtAttachmentByPwtId(projectWithTutor.getId());

        // PWT 게시글이 승인 되었는지 확인 하는 예외 처리
        if (!projectWithTutor.getApproval().equals(Approval.APPROVED)) {
            throw new ApiException(ErrorStatus._ACCESS_PERMISSION_DENIED);
        }

        return ProjectWithTutorDetailResponse.of(
                projectWithTutor.getTitle(),
                projectWithTutor.getDescription(),
                projectWithTutor.getPrice(),
                projectWithTutor.getStatus().getStatus(),
                projectWithTutor.getDeadline(),
                projectWithTutor.getMaxParticipants(),
                projectWithTutor.getLevel().getLevel(),
                projectWithTutor.getUser().getUsername(),
                pwtAttachment.getImageURL()
        );
    }

    // 튜터랑 함께하는 협업 프로젝트 게시글 다건 조회 (승인이 완료된 게시글 다건 조회)
    public Page<ProjectWithTutorListResponse> getAllProjectWithTutors(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);


        Page<ProjectWithTutorResponseDto> projectWithTutors = projectWithTutorRepository.findAllApprovedProjectWithTutor(Approval.APPROVED, pageable)
                .filter(p -> !p.isEmpty())
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_PROJECT_WITH_TUTOR));

        return projectWithTutors.map(p -> ProjectWithTutorListResponse.of(
                p.getId(),
                p.getTitle(),
                p.getPrice(),
                p.getStatus().getStatus(),
                p.getDeadline(),
                p.getMaxParticipants(),
                p.getLevel().getLevel(),
                p.getUser().getUsername()
        ));
    }

    // 튜터랑 함께하는 협업 프로젝트 게시글 수정
    @Transactional
    public String updateProjectWithTutor(
            AuthUser authUser,
            Long projectId,
            MultipartFile file,
            ProjectWithTutorUpdateRequest projectWithTutorUpdateRequest
    ) {
        // 사용자 객체 가져오기
        User user = userService.findByUserId(authUser.getId());

        // PWT 게시글 객체 가져오기
        ProjectWithTutor projectWithTutor = projectWithTutorRepository.findById(projectId)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_PROJECT_WITH_TUTOR));

        // 게시글 작성자와 현재 로그인된 사용자 일치 여부 예외 처리
        if (!user.getId().equals(projectWithTutor.getUser().getId())) {
            throw new ApiException(ErrorStatus._HAS_NOT_ACCESS_PERMISSION);
        }

        // 추가된 파일이 있는지 확인
        if (file != null && !file.isEmpty()) {
            // PWT 첨부파일 객체 가져오기
            PWTAttachment pwtAttachment = pwtAttachmentService.findPwtAttachmentByPwtId(projectWithTutor.getId());

            if (pwtAttachment == null) {
                s3Service.uploadFile(file, user, projectWithTutor);
            } else {
                // PWT 첨부파일 수정
                s3Service.updateUploadFile(file, pwtAttachment, projectWithTutor);
            }

        }

        // 변경사항 업데이트
        projectWithTutor.update(
                projectWithTutorUpdateRequest.getTitle(),
                projectWithTutorUpdateRequest.getDescription(),
                projectWithTutorUpdateRequest.getPrice(),
                projectWithTutorUpdateRequest.getDeadline(),
                projectWithTutorUpdateRequest.getMaxParticipants(),
                Level.of(projectWithTutorUpdateRequest.getLevel()),
                user,
                Category.of(projectWithTutorUpdateRequest.getCategory())
        );

        return String.format("%s 게시글이 수정되었습니다.", projectWithTutor.getTitle());
    }

    // 튜터랑 함께하는 협업 프로젝트 게시글 삭제
    @Transactional
    public String deleteProjectWithTutor(AuthUser authUser, Long projectId) {

        // 사용자 객체 가져오기
        User user = userService.findByUserId(authUser.getId());

        // PWT 게시글 객체 가져오기
        ProjectWithTutor projectWithTutor = projectWithTutorRepository.findById(projectId)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_PROJECT_WITH_TUTOR));

        // PWT 첨부파일 객체 가져오기
        PWTAttachment pwtAttachment = pwtAttachmentService.findPwtAttachmentByPwtId(projectWithTutor.getId());

        // 게시글 작성자와 현재 로그인된 사용자 일치 여부 예외 처리
        if (!user.getId().equals(projectWithTutor.getUser().getId())) {
            throw new ApiException(ErrorStatus._HAS_NOT_ACCESS_PERMISSION);
        }

        // S3에 첨부파일 삭제
        s3Service.delete(pwtAttachment.getFileName());

        // PWT 첨부파일 삭제
        pwtAttachmentService.deletePwtAttachment(pwtAttachment);

        // PWT 게시글 삭제
        projectWithTutorRepository.delete(projectWithTutor);

        return String.format("%s 게시글을 삭제하였습니다.", projectWithTutor.getTitle());
    }

    /**
     * Search에서 사용
     */
    public List<IntegrationSearchResponse> getProjectWithTutor(Specification<ProjectWithTutor> spec) {
        List<ProjectWithTutor> pwts = projectWithTutorRepository.findAll(spec);
        return SearchResponseUtil.wrapResponse(BoardType.PWT, pwts);
    }

    public Page<IntegrationSearchResponse> getProjectWithTutorPage(Specification<ProjectWithTutor> spec, PageRequest pageable) {
        Page<ProjectWithTutor> pwtPage = projectWithTutorRepository.findAll(spec, pageable);
        List<IntegrationSearchResponse> response = SearchResponseUtil.wrapResponse(
                BoardType.PWT, pwtPage.getContent()
        );
        return new PageImpl<>(response, pageable, pwtPage.getTotalElements());
    }
}
