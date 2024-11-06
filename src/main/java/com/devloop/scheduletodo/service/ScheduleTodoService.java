package com.devloop.scheduletodo.service;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.pwt.entity.ProjectWithTutor;
import com.devloop.scheduleboard.entity.ScheduleBoard;
import com.devloop.scheduleboard.repository.BoardAssignmentRepository;
import com.devloop.scheduleboard.service.ScheduleBoardService;
import com.devloop.scheduletodo.request.ScheduleTodoRequest;
import com.devloop.scheduletodo.response.ScheduleTodoResponse;
import com.devloop.scheduletodo.response.ScheduleTodoSimpleResponse;
import com.devloop.scheduletodo.entity.ScheduleTodo;
import com.devloop.scheduletodo.repository.ScheduleTodoRepository;
import com.devloop.user.entity.User;
import com.devloop.user.service.UserService;
import jakarta.persistence.OptimisticLockException;
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

        //존재하는 스케줄보드,유저인지 확인
        ScheduleBoard scheduleBoard = scheduleBoardService.findByScheduleBoardById(scheduleBoardId);
        User currentUser = userService.findByUserId(authUser.getId());

        //현재 유저가 해당 pwt 게시글의 튜터인지 확인(글작성자=튜터는 일정작성 가능)
        ProjectWithTutor projectWithTutor = scheduleBoard.getProjectWithTutor();
        if (currentUser.equals(projectWithTutor.getUser())) {
            // 별도 권한 확인 없이 일정 생성 허용
            ScheduleTodo scheduleTodo = ScheduleTodo.of(
                    scheduleBoard,
                    currentUser, //작성자를 currentuser로 설정
                    scheduleTodoRequest.getTitle(),
                    scheduleTodoRequest.getContent(),
                    scheduleTodoRequest.getStartDate(),
                    scheduleTodoRequest.getEndDate()
            );

            scheduleTodoRepository.save(scheduleTodo);
            return ScheduleTodoResponse.of(
                    scheduleTodo.getId(),
                    currentUser.getUsername(),
                    scheduleTodo.getTitle(),
                    scheduleTodo.getContent(),
                    scheduleTodo.getStartDate(),
                    scheduleTodo.getEndDate()
            );
        }

        // 현재 유저가 해당 스케줄 보드에 속한 PWT 게시글의 유저인지 확인 BoardAssignment로 접근 권한 확인
        boolean hasAccess = boardAssignmentRepository.existsByScheduleBoardAndPurchase_User(scheduleBoard, currentUser);
        if (!hasAccess) {
            throw new ApiException(ErrorStatus._PERMISSION_DENIED);
        }

        //접근권한이 있으면 scheduleTodo 생성
        ScheduleTodo scheduleTodo = ScheduleTodo.of(
                scheduleBoard,
                currentUser,
                scheduleTodoRequest.getTitle(),
                scheduleTodoRequest.getContent(),
                scheduleTodoRequest.getStartDate(),
                scheduleTodoRequest.getEndDate()
        );

        scheduleTodoRepository.save(scheduleTodo);
        return ScheduleTodoResponse.of(
                scheduleTodo.getId(),
                currentUser.getUsername(),
                scheduleTodo.getTitle(),
                scheduleTodo.getContent(),
                scheduleTodo.getStartDate(),
                scheduleTodo.getEndDate()
        );
    }

    //스케줄보드에 속한 scheduleTodo목록 다건조회
    public List<ScheduleTodoSimpleResponse> getTodoByScheduleBoard(Long scheduleBoardId) {
        ScheduleBoard scheduleBoard = scheduleBoardService.findByScheduleBoardById(scheduleBoardId);

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
        try {
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
                        scheduleTodo.getCreatedBy().getUsername(),
                        scheduleTodo.getTitle(),
                        scheduleTodo.getContent(),
                        scheduleTodo.getStartDate(),
                        scheduleTodo.getEndDate()
                );
            }
            //일반 유저 권한 확인: 본인이 쓴 일정인지 확인
            if (!scheduleTodo.getCreatedBy().equals(currentUser)) {
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
                    scheduleTodo.getCreatedBy().getUsername(),
                    scheduleTodo.getTitle(),
                    scheduleTodo.getContent(),
                    scheduleTodo.getStartDate(),
                    scheduleTodo.getEndDate()
            );
        } catch (OptimisticLockException e) {
            //낙관적 락 예외처리
            throw new ApiException(ErrorStatus._CONFLICT);
        }
    }

    //삭제기능
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
        // 일반 유저의 권한 확인
        if (!scheduleTodo.getCreatedBy().equals(currentUser)) {
            throw new ApiException(ErrorStatus._PERMISSION_DENIED);
        }

        scheduleTodoRepository.delete(scheduleTodo);
    }

}
