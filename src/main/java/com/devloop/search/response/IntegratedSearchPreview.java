package com.devloop.search.response;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class IntegratedSearchPreview {

    private List<IntegrationSearchResponse> partyPreview;
    private List<IntegrationSearchResponse> communityPreview;
    private List<IntegrationSearchResponse> pwtPreview;
    private List<IntegrationSearchResponse> lecturePreivew;
    private long totalPartyCount;
    private long totalCommunityCount;
    private long totalPwtCount;
    private long totalLectureCount;
}
