package com.devloop.scheduleTodo.service;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
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
}
