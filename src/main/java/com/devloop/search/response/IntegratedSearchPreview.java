package com.devloop.search.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IntegratedSearchPreview {

    private List<IntegrationSearchResponse> partyPreview;
    private List<IntegrationSearchResponse> communityPreview;
    private List<IntegrationSearchResponse> pwtPreview;
    private long totalPartyCount;
    private long totalCommunityCount;
    private long totalPwtCount;
}
