package com.devloop.scheduleTodo.controller;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.ApiResponse;
import com.devloop.scheduleTodo.dto.request.ScheduleTodoRequest;
import com.devloop.scheduleTodo.dto.response.ScheduleTodoResponse;
import com.devloop.scheduleTodo.service.ScheduleTodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/{scheduleBoardId}/scheduleTodos")
public class ScheduleTodoController {

    private final ScheduleTodoService scheduleTodoService;

    //일정 생성
    @PostMapping
    public ApiResponse<ScheduleTodoResponse> createScheduleTodo(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long scheduleBoardId, @RequestBody @Valid ScheduleTodoRequest scheduleTodoRequest) {
        return ApiResponse.ok(scheduleTodoService.createScheduleTodo(scheduleBoardId, scheduleTodoRequest, authUser));
    }

    //일정 단건 조회
    @GetMapping("/{scheduletodoId}")
    public ApiResponse<ScheduleTodoResponse> getScheduleTodo(@PathVariable Long scheduleTodoId){
        return ApiResponse.ok(scheduleTodoService.getScheduleTodo(scheduleTodoId));
    }

}
