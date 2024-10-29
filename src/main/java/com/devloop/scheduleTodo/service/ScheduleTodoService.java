package com.devloop.scheduleTodo.service;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.pwt.entity.ProjectWithTutor;
import com.devloop.scheduleBoard.entity.ScheduleBoard;
import com.devloop.scheduleBoard.service.ScheduleBoardService;
import com.devloop.scheduleTodo.dto.request.ScheduleTodoRequest;
import com.devloop.scheduleTodo.dto.response.ScheduleTodoResponse;
import com.devloop.scheduleTodo.dto.response.ScheduleTodoSimpleResponse;
import com.devloop.scheduleTodo.entity.ScheduleTodo;
import com.devloop.scheduleTodo.repository.ScheduleTodoRepository;
import com.devloop.user.entity.User;
import com.devloop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleTodoService {
    private final ScheduleTodoRepository scheduleTodoRepository;
    private final UserService userService;
    private final ScheduleBoardService scheduleBoardService;

    //생성
    @Transactional
    public ScheduleTodoResponse createScheduleTodo(Long scheduleBoardId, ScheduleTodoRequest scheduleTodoRequest, AuthUser authUser) {

        ScheduleBoard scheduleBoard = scheduleBoardService.findByScheduleBoardId(scheduleBoardId);
        User createdBy = userService.findByUserId(authUser.getId());

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

    //스케줄보드에 속한 scheduleTodo목록 조회
    public List<ScheduleTodoSimpleResponse> getTodoByScheduleBoard(Long scheduleBoardId) {
        ScheduleBoard scheduleBoard = scheduleBoardService.findByScheduleBoardId(scheduleBoardId);

        return scheduleTodoRepository.findByScheduleBoard(scheduleBoard)
                .stream()
                .map(todo -> ScheduleTodoSimpleResponse.of(todo.getTitle(), todo.getStartDate(), todo.getEndDate()))
                .collect(Collectors.toList());
    }

    //scheduleTodo 단건조회
    public ScheduleTodoResponse getScheduleTodo(Long scheduleTodoId) {
        ScheduleTodo scheduleTodo = scheduleTodoRepository.findById(scheduleTodoId)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_SCHEDULE_TODO));

        return ScheduleTodoResponse.of(
                scheduleTodo.getId(),
                scheduleTodo.getCreatedBy().getUsername(),
                scheduleTodo.getTitle(),
                scheduleTodo.getContent(),
                scheduleTodo.getStartDate(),
                scheduleTodo.getEndDate()
        );
    }

    //수정
    @Transactional
    public ScheduleTodoResponse updateScheduleTodo(AuthUser authUser, Long scheduleTodoId, ScheduleTodoRequest scheduleTodoRequest) {
        ScheduleTodo scheduleTodo = scheduleTodoRepository.findById(scheduleTodoId)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_SCHEDULE_TODO));

        ProjectWithTutor project = scheduleTodo.getScheduleBoard().getProjectWithTutor();
        User currentUser = userService.findByUserId(authUser.getId());

        //권한체크 : 일반유저는 본인 것만, 튜터는 모두 수정 가능
        if (!currentUser.equals(scheduleTodo.getCreatedBy()) && !currentUser.equals(project.getUser())) {
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
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_SCHEDULE_TODO));
        ProjectWithTutor project = scheduleTodo.getScheduleBoard().getProjectWithTutor();
        User currentUser = userService.findByUserId(authUser.getId());

        //권한 체크 : 일반 유저는 본인 글만, 튜터는 모두 삭제 가능
        if (!currentUser.equals(scheduleTodo.getCreatedBy()) && !currentUser.equals(project.getUser())) {
            throw new ApiException(ErrorStatus._PERMISSION_DENIED);
        }
        scheduleTodoRepository.delete(scheduleTodo);
    }

    //Util
    public List<ScheduleTodoSimpleResponse> getSimpleResponsesByScheduleBoard(ScheduleBoard scheduleBoard) {
        return scheduleTodoRepository.findByScheduleBoard(scheduleBoard)
                .stream()
                .map(todo -> ScheduleTodoSimpleResponse.of(todo.getTitle(), todo.getStartDate(), todo.getEndDate()))
                .collect(Collectors.toList());
    }
}
