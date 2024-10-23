package com.devloop.community.dto.request;

import com.devloop.community.entity.Category;
import com.devloop.community.entity.ResolveStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class CommunitySaveRequest {
    private String title;
    private String content;
    private ResolveStatus status; //게시글 해결 상태 (SOLVED, UNSOLVED)
    private Category category;
}
