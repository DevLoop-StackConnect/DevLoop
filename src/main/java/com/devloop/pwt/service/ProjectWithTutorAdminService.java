package com.devloop.pwt.service;

import com.devloop.common.apipayload.dto.ProjectWithTutorResponseDto;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.enums.Approval;
import com.devloop.common.exception.ApiException;
import com.devloop.pwt.entity.ProjectWithTutor;
import com.devloop.pwt.repository.ProjectWithTutorRepository;
import com.devloop.pwt.response.ProjectWithTutorDetailAdminResponse;
import com.devloop.pwt.response.ProjectWithTutorListAdminResponse;
import com.devloop.pwt.response.ProjectWithTutorListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectWithTutorAdminService {

    private final ProjectWithTutorRepository projectWithTutorRepository;

    // PWT 게시글 승인 (ADMIN)
    @Transactional
    public String changeApproval(Long pwtId) {

        // PWT 게시글 객체 가져오기
        ProjectWithTutor projectWithTutor = projectWithTutorRepository.findById(pwtId)
                .orElseThrow(()->new ApiException(ErrorStatus._NOT_FOUND_PROJECT_WITH_TUTOR));

        // PWT 게시글 승인여부 상태 변경
        projectWithTutor.changeApproval(Approval.APPROVED);

        return String.format("%s 게시글이 승인 되었습니다.", projectWithTutor.getTitle());
    }

    // 튜터랑 함께하는 협업 프로젝트 게시글 단건 조회(ADMIN : User 정보 포함 단건 조회)
    public ProjectWithTutorDetailAdminResponse getProjectWithTutor(Long projectId) {
        // PWT 게시글 객체 가져오기
        ProjectWithTutor projectWithTutor = projectWithTutorRepository.findById(projectId)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_PROJECT_WITH_TUTOR));

        return ProjectWithTutorDetailAdminResponse.from(projectWithTutor);
    }

    // 튜터랑 함께하는 협업 프로젝트 승인되지 않은 게시글 다건 조회 (ADMIN : TEXT 타입 데이터 제외 다건 조회)
    public Page<ProjectWithTutorListAdminResponse> getAllProjectWithTutors(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);


        Page<ProjectWithTutorResponseDto> projectWithTutors = projectWithTutorRepository.findAllWaiteProjectWithTutor(Approval.WAITE, pageable)
                .filter(p->!p.isEmpty())
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_PROJECT_WITH_TUTOR));

        return projectWithTutors.map(ProjectWithTutorListAdminResponse::from);
    }
}
