package com.devloop.search.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class IntegratedSearchPreview implements Serializable {
    private List<IntegrationSearchResponse> partyPreview;
    private List<IntegrationSearchResponse> communityPreview;
    private List<IntegrationSearchResponse> pwtPreview;
    private List<IntegrationSearchResponse> lecturePreivew;
    private long totalPartyCount;
    private long totalCommunityCount;
    private long totalPwtCount;
    private long totalLectureCount;

    // 기본 생성자 추가
    @JsonCreator
    public IntegratedSearchPreview(
            @JsonProperty("partyPreview") List<IntegrationSearchResponse> partyPreview,
            @JsonProperty("communityPreview") List<IntegrationSearchResponse> communityPreview,
            @JsonProperty("pwtPreview") List<IntegrationSearchResponse> pwtPreview,
            @JsonProperty("lecturePreivew") List<IntegrationSearchResponse> lecturePreivew,
            @JsonProperty("totalPartyCount") long totalPartyCount,
            @JsonProperty("totalCommunityCount") long totalCommunityCount,
            @JsonProperty("totalPwtCount") long totalPwtCount,
            @JsonProperty("totalLectureCount") long totalLectureCount) {
        this.partyPreview = partyPreview;
        this.communityPreview = communityPreview;
        this.pwtPreview = pwtPreview;
        this.lecturePreivew = lecturePreivew;
        this.totalPartyCount = totalPartyCount;
        this.totalCommunityCount = totalCommunityCount;
        this.totalPwtCount = totalPwtCount;
        this.totalLectureCount = totalLectureCount;
    }
}