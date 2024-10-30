package com.devloop.scheduleTodo.service;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.pwt.entity.ProjectWithTutor;
import com.devloop.scheduleBoard.entity.ScheduleBoard;
import com.devloop.scheduleBoard.repository.BoardAssignmentRepository;
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
    private final BoardAssignmentRepository boardAssignmentRepository;

    //생성
    @Transactional
    public ScheduleTodoResponse createScheduleTodo(Long scheduleBoardId, ScheduleTodoRequest scheduleTodoRequest, AuthUser authUser) {

        ScheduleBoard scheduleBoard = scheduleBoardService.findByScheduleBoardId(scheduleBoardId);
        User currentUser = userService.findByUserId(authUser.getId());

        //현재 유저가 해당 pwt 게시글의 튜터인지 확인(글작성자=튜터는 일정작성 가능)
        ProjectWithTutor projectWithTutor = scheduleBoard.getProjectWithTutor();
        if (currentUser.equals(projectWithTutor.getUser())) {
            // 별도 권한 확인 없이 일정 생성 허용
            ScheduleTodo scheduleTodo = ScheduleTodo.of(
                    scheduleBoard,
                    scheduleTodoRequest.getTitle(),
                    scheduleTodoRequest.getContent(),
                    scheduleTodoRequest.getStartDate(),
                    scheduleTodoRequest.getEndDate()
            );

            scheduleTodoRepository.save(scheduleTodo);
            return ScheduleTodoResponse.of(
                    scheduleTodo.getId(),
                    scheduleTodo.getTitle(),
                    scheduleTodo.getContent(),
                    scheduleTodo.getStartDate(),
                    scheduleTodo.getEndDate()
            );
        }

        // 현재 유저가 해당 스케줄 보드에 속한 PWT 게시글의 유저인지 확인 BoardAssignment로 접근 권한 확인
        boolean hasAccess = boardAssignmentRepository.existsByScheduleBoardAndUserId(scheduleBoard, currentUser.getId());
        if (!hasAccess) {
            throw new ApiException(ErrorStatus._PERMISSION_DENIED);
        }

        //접근권한이 있으면 scheduleTodo 생성
        ScheduleTodo scheduleTodo = ScheduleTodo.of(
                scheduleBoard,
                scheduleTodoRequest.getTitle(),
                scheduleTodoRequest.getContent(),
                scheduleTodoRequest.getStartDate(),
                scheduleTodoRequest.getEndDate()
        );

        scheduleTodoRepository.save(scheduleTodo);
        return ScheduleTodoResponse.of(
                scheduleTodo.getId(),
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

        ScheduleBoard scheduleBoard = scheduleTodo.getScheduleBoard();
        ProjectWithTutor projectWithTutor = scheduleBoard.getProjectWithTutor();
        User currentUser = userService.findByUserId(authUser.getId());


        //튜터는 모든 scheduleTodo수정가능
        if (currentUser.equals(projectWithTutor.getUser())) {
            scheduleTodo.updateScheduleTodo(
                    scheduleTodoRequest.getTitle(),
                    scheduleTodoRequest.getContent(),
                    scheduleTodoRequest.getStartDate(),
                    scheduleTodoRequest.getEndDate()
            );

            return ScheduleTodoResponse.of(
                    scheduleTodo.getId(),
                    scheduleTodo.getTitle(),
                    scheduleTodo.getContent(),
                    scheduleTodo.getStartDate(),
                    scheduleTodo.getEndDate()
            );
        }
        boolean hasAcess = boardAssignmentRepository.existsByScheduleBoardAndUserId(scheduleBoard, currentUser.getId());
        if (!hasAcess) {
            throw new ApiException(ErrorStatus._PERMISSION_DENIED);
        }
        //권한 확인 후에 수정로직
        scheduleTodo.updateScheduleTodo(
                scheduleTodoRequest.getTitle(),
                scheduleTodoRequest.getContent(),
                scheduleTodoRequest.getStartDate(),
                scheduleTodoRequest.getEndDate()
        );
        return ScheduleTodoResponse.of(
                scheduleTodo.getId(),
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

        ScheduleBoard scheduleBoard = scheduleTodo.getScheduleBoard();
        ProjectWithTutor projectWithTutor = scheduleBoard.getProjectWithTutor();
        User currentUser = userService.findByUserId(authUser.getId());


        // 튜터 권한 확인: 프로젝트 작성자(튜터)라면 모든 스케줄Todo 삭제 가능
        if (currentUser.equals(projectWithTutor.getUser())) {
            scheduleTodoRepository.delete(scheduleTodo);
            return;
        }
        // 일반 유저의 권한 확인: BoardAssignment 통해 권한 확인
        boolean hasAccess = boardAssignmentRepository.existsByScheduleBoardAndUserId(scheduleBoard, currentUser.getId());
        if (!hasAccess) {
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
