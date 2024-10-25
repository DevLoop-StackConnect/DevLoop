package com.devloop.pwt.service;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.dto.ProjectWithTutorResponseDto;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.enums.Approval;
import com.devloop.common.exception.ApiException;
import com.devloop.pwt.entity.ProjectWithTutor;
import com.devloop.pwt.enums.Level;
import com.devloop.pwt.enums.ProjectWithTutorStatus;
import com.devloop.pwt.repository.ProjectWithTutorRepository;
import com.devloop.pwt.request.ProjectWithTutorSaveRequest;
import com.devloop.pwt.request.ProjectWithTutorUpdateRequest;
import com.devloop.pwt.response.ProjectWithTutorDetailResponse;
import com.devloop.pwt.response.ProjectWithTutorListResponse;
import com.devloop.user.entity.User;
import com.devloop.user.enums.UserRole;
import com.devloop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectWithTutorService {

    private final ProjectWithTutorRepository projectWithTutorRepository;
    private final UserRepository userRepository;

    // 튜터랑 함께하는 협업 프로젝트 게시글 생성
    @Transactional
    public String saveProjectWithTutor(
            AuthUser authUser,
            MultipartFile file,
            ProjectWithTutorSaveRequest projectWithTutorSaveRequest
    ) {
        // 사용자 객체 가져오기
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_USER));

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
                user
        );
        projectWithTutorRepository.save(projectWithTutor);

        // todo : 첨부파일 저장

        return String.format("%s 님의 튜터랑 함께하는 협업 프로젝트 게시글이 작성 완료되었습니다. 승인까지 3~5일 정도 소요될 수 있습니다.", user.getUsername());
    }

    // 튜터랑 함께하는 협업 프로젝트 게시글 단건 조회 (승인이 완료된 게시글 단건 조회)
    public ProjectWithTutorDetailResponse getProjectWithTutor(Long projectId) {
        // PWT 게시글 객체 가져오기
        ProjectWithTutor projectWithTutor = projectWithTutorRepository.findById(projectId)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_PROJECT_WITH_TUTOR));

        // PWT 게시글이 승인 되었는지 확인 하는 예외 처리
        if (projectWithTutor.getApproval().equals(Approval.APPROVED)) {
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
                projectWithTutor.getUser().getUsername()
        );
    }

    // 튜터랑 함께하는 협업 프로젝트 게시글 다건 조회 (승인이 완료된 게시글 다건 조회)
    public Page<ProjectWithTutorListResponse> getAllProjectWithTutors(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);


        Page<ProjectWithTutorResponseDto> projectWithTutors = projectWithTutorRepository.findAllApprovedProjectWithTutor(Approval.APPROVED, pageable)
                .filter(p->!p.isEmpty())
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_PROJECT_WITH_TUTOR));

        return projectWithTutors.map(p->ProjectWithTutorListResponse.of(
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
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_USER));

        // PWT 게시글 객체 가져오기
        ProjectWithTutor projectWithTutor = projectWithTutorRepository.findById(projectId)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_PROJECT_WITH_TUTOR));

        // 게시글 작성자와 현재 로그인된 사용자 일치 여부 예외 처리
        if (!user.getId().equals(projectWithTutor.getUser().getId())) {
            throw new ApiException(ErrorStatus._HAS_NOT_ACCESS_PERMISSION);
        }

        // 변경사항 업데이트
        projectWithTutor.update(
                projectWithTutorUpdateRequest.getTitle(),
                projectWithTutorUpdateRequest.getDescription(),
                projectWithTutorUpdateRequest.getPrice(),
                projectWithTutorUpdateRequest.getDeadline(),
                projectWithTutorUpdateRequest.getMaxParticipants(),
                Level.of(projectWithTutorUpdateRequest.getLevel()),
                user
        );

        return String.format("%s 게시글이 수정되었습니다.", projectWithTutor.getTitle());
    }

    // 튜터랑 함께하는 협업 프로젝트 게시글 삭제
    @Transactional
    public String deleteProjectWithTutor(AuthUser authUser, Long projectId) {

        // 사용자 객체 가져오기
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_USER));

        // PWT 게시글 객체 가져오기
        ProjectWithTutor projectWithTutor = projectWithTutorRepository.findById(projectId)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_PROJECT_WITH_TUTOR));

        // 게시글 작성자와 현재 로그인된 사용자 일치 여부 예외 처리
        if (!user.getId().equals(projectWithTutor.getUser().getId())) {
            throw new ApiException(ErrorStatus._HAS_NOT_ACCESS_PERMISSION);
        }

        // PWT 게시글 삭제
        projectWithTutorRepository.delete(projectWithTutor);

        return String.format("%s 게시글을 삭제하였습니다.", projectWithTutor.getTitle());
    }

}
