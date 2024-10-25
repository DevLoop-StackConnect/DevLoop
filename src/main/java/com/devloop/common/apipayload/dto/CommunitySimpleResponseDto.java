package com.devloop.common.apipayload.dto;

import com.devloop.common.enums.Category;
import com.devloop.community.entity.ResolveStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class CommunitySimpleResponseDto {
    private final Long communityId;
    private final String title;
    private final ResolveStatus status;
    private final Category category;
}
