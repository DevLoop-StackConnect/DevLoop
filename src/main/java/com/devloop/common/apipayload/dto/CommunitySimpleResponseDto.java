package com.devloop.common.apipayload.dto;

import com.devloop.common.enums.Category;
import com.devloop.community.entity.ResolveStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CommunitySimpleResponseDto {
    private final Long communityId;
    private final String title;
    private final ResolveStatus status;
    private final Category category;
}
