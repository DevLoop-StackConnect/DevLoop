package com.devloop.scheduleBoard.service;

import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.pwt.entity.ProjectWithTutor;
import com.devloop.pwt.service.ProjectWithTutorService;
import com.devloop.scheduleBoard.dto.response.ScheduleBoardResponse;
import com.devloop.scheduleBoard.entity.BoardAssignment;
import com.devloop.scheduleBoard.entity.ScheduleBoard;
import com.devloop.scheduleBoard.repository.BoardAssignmentRepository;
import com.devloop.scheduleBoard.repository.ScheduleBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleBoardService {
    private final ScheduleBoardRepository scheduleBoardRepository;
    private final ProjectWithTutorService projectWithTutorService;

    //PWT 승인 시 스케줄보드 생성 메서드
    @Transactional
    public ScheduleBoard createScheduleBoard(ProjectWithTutor projectWithTutor) {
        //스케줄보드객체 생성해서 db에 저장
        ScheduleBoard scheduleBoard = ScheduleBoard.of(projectWithTutor);
        return scheduleBoardRepository.save(scheduleBoard);
    }

    //스케줄 보드 조회
    @Transactional
    public ScheduleBoardResponse getScheduleBoard(Long pwtId) {
        //있는 PWT게시글인지 확인
        ProjectWithTutor projectWithTutor = projectWithTutorService.findByPwtId(pwtId);
        //존재하는 스케줄보드인지 확인
        ScheduleBoard scheduleBoard = scheduleBoardRepository.findByProjectWithTutor(projectWithTutor)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_SCHEDULE_BOARD));


        return ScheduleBoardResponse.of(
                scheduleBoard.getId(),
                projectWithTutor.getId(),
                projectWithTutor.getUser().getUsername()
//                scheduleBoard.getManagerTutor().getUsername()
        );
    }

    //util
    public ScheduleBoard findByScheduleBoardId(Long scheduleBoardId) {
        return scheduleBoardRepository.findById(scheduleBoardId)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_SCHEDULE_BOARD));
    }
}
