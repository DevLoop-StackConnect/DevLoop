package com.devloop.pwt.service;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.enums.Approval;
import com.devloop.common.exception.ApiException;
import com.devloop.pwt.entity.ProjectWithTutor;
import com.devloop.pwt.enums.Level;
import com.devloop.pwt.enums.ProjectWithTutorStatus;
import com.devloop.pwt.repository.ProjectWithTutorRepository;
import com.devloop.pwt.request.ProjectWithTutorSaveRequest;
import com.devloop.user.entity.User;
import com.devloop.user.enums.UserRole;
import com.devloop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
                .orElseThrow(()-> new ApiException(ErrorStatus._NOT_FOUND_USER));

        // 요청한 사용자의 권한이 USER일 경우 예외 처리
        if(user.getUserRole().equals(UserRole.ROLE_USER)){
            throw new ApiException(ErrorStatus._HAS_NOT_ACCESS_PERMISSION);
        }

        // PWT 게시글 객체 생성
        ProjectWithTutor projectWithTutor = ProjectWithTutor.from(
                projectWithTutorSaveRequest.getTitle(),
                projectWithTutorSaveRequest.getDescription(),
                projectWithTutorSaveRequest.getPrice(),
                ProjectWithTutorStatus.IN_PROGRESS,
                projectWithTutorSaveRequest.getDeadline(),
                projectWithTutorSaveRequest.getMaxParticipants(),
                Level.of(projectWithTutorSaveRequest.getLevel()),
                Approval.WAITE,
                user
        );
        projectWithTutorRepository.save(projectWithTutor);

        // todo : 첨부파일 저장

        return String.format("%s 님의 튜터랑 함께하는 협업 프로젝트 게시글이 작성 완료되었습니다. 승인까지 3~5일 정도 소요될 수 있습니다.", user.getUsername());
    }
}
