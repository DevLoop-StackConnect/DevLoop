package com.devloop.tutor.service;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.tutor.entity.TutorRequest;
import com.devloop.tutor.enums.TutorRequestStatus;
import com.devloop.tutor.repository.TutorRequestRepository;
import com.devloop.tutor.response.TutorRequestResponse;
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

    // 튜터 신청 요청 조회 (ADMIN : 승인되지 않은 튜터 신청 요청 다건 조회)
    public Page<TutorRequestResponse> getAllTutorRequest(int page, int size) {
        // 페이징 지정
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<TutorRequest> requests = requestRepository.findAllByStatus(pageable, TutorRequestStatus.WAITE)
                .filter(r -> !r.isEmpty())
                .orElseThrow(() -> new ApiException(ErrorStatus._TUTOR_REQUEST_NOT_EXIST));

        return requests.map(TutorRequestResponse::from);
    }


}
