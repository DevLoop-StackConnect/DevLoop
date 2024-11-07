package com.devloop.scheduleboard.controller;

import com.devloop.common.apipayload.ApiResponse;
import com.devloop.scheduleboard.response.ScheduleBoardResponse;
import com.devloop.scheduleboard.service.ScheduleBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ScehduleBoardController {
    private final ScheduleBoardService scheduleBoardService;

    //pwt 게시판의 게시글에서 scheduleBoard 조회
    @GetMapping("/search/v2/scheduleBoards/{pwtId}")
    public ApiResponse<ScheduleBoardResponse> getScheduleBoard(@PathVariable Long pwtId) {
        return ApiResponse.ok(scheduleBoardService.getScheduleBoard(pwtId));
    }
}
