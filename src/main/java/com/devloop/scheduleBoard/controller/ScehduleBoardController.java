package com.devloop.scheduleBoard.controller;

import com.devloop.attachment.service.PWTAttachmentService;
import com.devloop.common.apipayload.ApiResponse;
import com.devloop.scheduleBoard.dto.response.ScheduleBoardResponse;
import com.devloop.scheduleBoard.service.ScheduleBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/scheduleBoards")
public class ScehduleBoardController {
    private final ScheduleBoardService scheduleBoardService;
    private final PWTAttachmentService pWTAttachmentService;

    //pwt 게시판의 게시글에서 scheduleBoard 조회
    @GetMapping("{pwtId}")
    public ApiResponse<ScheduleBoardResponse> getScheduleBoard(@PathVariable Long pwtId) {
        return ApiResponse.ok(scheduleBoardService.getScheduleBoard(pwtId));
    }

}
