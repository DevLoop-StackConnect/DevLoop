package com.devloop.community.dto.request;

import com.devloop.common.enums.Category;
import com.devloop.community.entity.ResolveStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommunityUpdateRequest {
    private String title;
    private String content;
    private ResolveStatus status;
    private Category category;
}
