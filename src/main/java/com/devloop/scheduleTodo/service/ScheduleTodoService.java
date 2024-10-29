package com.devloop.scheduleTodo.service;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.pwt.entity.ProjectWithTutor;
import com.devloop.pwt.repository.ProjectWithTutorRepository;
import com.devloop.scheduleBoard.entity.ScheduleBoard;
import com.devloop.scheduleBoard.repository.ScheduleBoardRepository;
import com.devloop.scheduleTodo.dto.request.ScheduleTodoRequest;
import com.devloop.scheduleTodo.dto.response.ScheduleTodoResponse;
import com.devloop.scheduleTodo.entity.ScheduleTodo;
import com.devloop.scheduleTodo.repository.ScheduleTodoRepository;
import com.devloop.user.entity.User;
import com.devloop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleTodoService {
    private final ScheduleTodoRepository scheduleTodoRepository;
    private final ScheduleBoardRepository scheduleBoardRepository;
    private final ProjectWithTutorRepository projectWithTutorRepository;
    private final UserRepository userRepository;

    @Transactional
    public ScheduleTodoResponse createScheduleTodo(Long scheduleBoardId, ScheduleTodoRequest scheduleTodoRequest, AuthUser authUser) {
        ScheduleBoard scheduleBoard = scheduleBoardRepository.findById(scheduleBoardId)
                .orElseThrow(()-> new ApiException(ErrorStatus._NOT_FOUND_SCHEDULE_BOARD));

        User createdBy = userRepository.findById(authUser.getId())
                .orElseThrow(()->new ApiException(ErrorStatus._NOT_FOUND_USER));

        ScheduleTodo scheduleTodo = ScheduleTodo.of(
                scheduleBoard,
                createdBy,
                scheduleTodoRequest.getTitle(),
                scheduleTodoRequest.getContent(),
                scheduleTodoRequest.getStartDate(),
                scheduleTodoRequest.getEndDate()
        );

        scheduleTodoRepository.save(scheduleTodo);
        return ScheduleTodoResponse.of(
                scheduleTodo.getId(),
                createdBy.getUsername(),
                scheduleTodo.getTitle(),
                scheduleTodo.getContent(),
                scheduleTodo.getStartDate(),
                scheduleTodo.getEndDate()
        );
    }

    public ScheduleTodoResponse getScheduleTodo(Long scheduleTodoId) {
        ScheduleTodo scheduleTodo = scheduleTodoRepository.findById(scheduleTodoId)
                .orElseThrow(()->new ApiException(ErrorStatus._NOT_FOUND_SCHEDULE_TODO));

        return ScheduleTodoResponse.of(
                scheduleTodo.getId(),
                scheduleTodo.getCreatedBy().getUsername(),
                scheduleTodo.getTitle(),
                scheduleTodo.getContent(),
                scheduleTodo.getStartDate(),
                scheduleTodo.getEndDate()
        );
    }

    @Transactional
    public ScheduleTodoResponse updateScheduleTodo(AuthUser authUser, Long scheduleTodoId, ScheduleTodoRequest scheduleTodoRequest) {
        ScheduleTodo scheduleTodo = scheduleTodoRepository.findById(scheduleTodoId)
                .orElseThrow(()-> new ApiException(ErrorStatus._NOT_FOUND_SCHEDULE_TODO));

        ProjectWithTutor project = scheduleTodo.getScheduleBoard().getProjectWithTutor();
        User currentUser = userRepository.findById(authUser.getId())
                .orElseThrow(()->new ApiException(ErrorStatus._NOT_FOUND_USER));

        //권한체크 : 일반유저는 본인 것만, 튜터는 모두 수정 가능
        if (!currentUser.equals(scheduleTodo.getCreatedBy())&&!currentUser.equals(project.getUser())){
            throw new ApiException(ErrorStatus._PERMISSION_DENIED);
        }
        scheduleTodo.updateScheduleTodo(
                scheduleTodoRequest.getTitle(),
                scheduleTodoRequest.getContent(),
                scheduleTodoRequest.getStartDate(),
                scheduleTodoRequest.getEndDate()
        );

        return ScheduleTodoResponse.of(
                scheduleTodo.getId(),
                currentUser.getUsername(),
                scheduleTodo.getTitle(),
                scheduleTodo.getContent(),
                scheduleTodo.getStartDate(),
                scheduleTodo.getEndDate()
        );

    }

    @Transactional
    public void deleteScheduleTodo(Long scheduleTodoId, AuthUser authUser) {
        ScheduleTodo scheduleTodo = scheduleTodoRepository.findById(scheduleTodoId)
                .orElseThrow(()->new ApiException(ErrorStatus._NOT_FOUND_SCHEDULE_TODO));
        ProjectWithTutor project = scheduleTodo.getScheduleBoard().getProjectWithTutor();
        User currentUser = userRepository.findById(authUser.getId())
                .orElseThrow(()-> new ApiException(ErrorStatus._NOT_FOUND_USER));

        //권한 체크 : 일반 유저는 본인 글만, 튜터는 모두 삭제 가능
        if (!currentUser.equals(scheduleTodo.getCreatedBy())&&!currentUser.equals(project.getUser())){
            throw new ApiException(ErrorStatus._PERMISSION_DENIED);
        }
        scheduleTodoRepository.delete(scheduleTodo);
    }
}
