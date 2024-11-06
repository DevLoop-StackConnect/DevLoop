package com.devloop.scheduletodo.controller;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.ApiResponse;
import com.devloop.scheduletodo.request.ScheduleTodoRequest;
import com.devloop.scheduletodo.response.ScheduleTodoResponse;
import com.devloop.scheduletodo.response.ScheduleTodoSimpleResponse;
import com.devloop.scheduletodo.service.ScheduleTodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/{scheduleBoardId}/scheduleTodos")
public class ScheduleTodoController {

    private final ScheduleTodoService scheduleTodoService;

    //일정 생성
    @PostMapping
    public ApiResponse<ScheduleTodoResponse> createScheduleTodo(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long scheduleBoardId,
            @RequestBody @Valid ScheduleTodoRequest scheduleTodoRequest) {
        return ApiResponse.ok(scheduleTodoService.createScheduleTodo(scheduleBoardId, scheduleTodoRequest, authUser));
    }

    //일정 다건 조회
    @GetMapping
    @PreAuthorize("permitAll()")
    public ApiResponse<List<ScheduleTodoSimpleResponse>> getTodoByScheduleBoard(@PathVariable Long scheduleBoardId) {
        return ApiResponse.ok(scheduleTodoService.getTodoByScheduleBoard(scheduleBoardId));
    }

    //일정 단건 조회
    @GetMapping("/{scheduleTodoId}")
    @PreAuthorize("permitAll()")
    public ApiResponse<ScheduleTodoResponse> getScheduleTodo(@PathVariable Long scheduleTodoId) {
        return ApiResponse.ok(scheduleTodoService.getScheduleTodo(scheduleTodoId));
    }

    //일정 수정
    @PatchMapping("/{scheduleTodoId}")
    public ApiResponse<ScheduleTodoResponse> updateScheduleTodo(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long scheduleTodoId,
            @RequestBody @Valid ScheduleTodoRequest scheduleTodoRequest) {
        return ApiResponse.ok(scheduleTodoService.updateScheduleTodo(authUser, scheduleTodoId, scheduleTodoRequest));
    }

    //일정 삭제
    @DeleteMapping("/{scheduleTodoId}")
    public ResponseEntity<Void> deleteScheduleTodo(@PathVariable Long scheduleTodoId, @AuthenticationPrincipal AuthUser authUser) {
        scheduleTodoService.deleteScheduleTodo(scheduleTodoId, authUser);
        return ResponseEntity.noContent().build();

    }

}
