package com.devloop.tutor.service;

import com.devloop.common.apipayload.dto.UserResponseDto;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.tutor.entity.TutorRequest;
import com.devloop.common.enums.Approval;
import com.devloop.tutor.repository.TutorRequestRepository;
import com.devloop.tutor.response.TutorRequestListAdminResponse;
import com.devloop.user.entity.User;
import com.devloop.user.enums.UserRole;
import com.devloop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TutorAdminService {

    private final TutorRequestRepository requestRepository;
    private final UserService userService;

    // 튜터 신청 요청 조회 (ADMIN : 승인되지 않은 튜터 신청 요청 다건 조회)
    public Page<TutorRequestListAdminResponse> getAllTutorRequest(int page, int size) {
        // 페이징 지정
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<TutorRequest> requests = requestRepository.findAllByStatus(pageable, Approval.WAITE);

        // 값이 비어있을때 예외 처리
        if(requests.isEmpty()){
            throw new ApiException(ErrorStatus._TUTOR_REQUEST_NOT_EXIST);
        }

        return requests.map(r -> TutorRequestListAdminResponse.of(
                r.getName(),
                r.getSubUrl(),
                r.getModifiedAt(),
                UserResponseDto.of(
                        r.getUser().getUsername(),
                        r.getUser().getEmail()
                )
        ));
    }

    // 튜터 신청 승인 (ADMIN : 튜터로 사용자 권한 변경)
    @Transactional
    public String changeUserRoleToTutor(Long userId) {
        // 사용자 객체 가져오기
        User user = userService.findByUserId(userId);

        // 튜터 신청 내역 가져오기
        TutorRequest tutorRequest = requestRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorStatus._TUTOR_REQUEST_NOT_EXIST));

        // 사용자 권한 변경
        user.changeUserRoleToTutor(UserRole.ROLE_TUTOR);
        // 튜터 신청 내역에서 승인여부 승인으로 변경
        tutorRequest.changeStatus(Approval.APPROVED);

        return String.format("%s 님의 튜터 신청이 승인되었습니다.", user.getUsername());
    }
}
