package com.devloop.scheduleBoard.service;

import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.pwt.entity.ProjectWithTutor;
import com.devloop.pwt.repository.ProjectWithTutorRepository;
import com.devloop.scheduleBoard.dto.response.ScheduleBoardResponse;
import com.devloop.scheduleBoard.entity.ScheduleBoard;
import com.devloop.scheduleBoard.repository.ScheduleBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleBoardService {
    private final ScheduleBoardRepository scheduleBoardRepository;
    private final ProjectWithTutorRepository projectWithTutorRepository;

    //PWT 승인 시 스케줄보드 생성 메서드
    @Transactional
    public ScheduleBoard createScheduleBoard(ProjectWithTutor projectWithTutor){
        //스케줄보드객체 생성해서 db에 저장
        ScheduleBoard scheduleBoard = ScheduleBoard.of(projectWithTutor,projectWithTutor.getUser());
        return scheduleBoardRepository.save(scheduleBoard);
    }

    //스케줄 보드 조회
    @Transactional
    public ScheduleBoardResponse getScheduleBoard(Long pwtId) {
        ProjectWithTutor projectWithTutor = projectWithTutorRepository.findById(pwtId)
                .orElseThrow(()-> new ApiException(ErrorStatus._NOT_FOUND_PROJECT_WITH_TUTOR));

        ScheduleBoard scheduleBoard = scheduleBoardRepository.findByProjectWithTutor(projectWithTutor)
                .orElseThrow(()->new ApiException(ErrorStatus._NOT_FOUND_SCHEDULE_BOARD));

        return ScheduleBoardResponse.of(
                scheduleBoard.getId(),
                projectWithTutorRepository.count(),
                scheduleBoard.getManagerTutor().getUsername()
                );
    }
}
