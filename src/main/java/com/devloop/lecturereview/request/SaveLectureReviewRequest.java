package com.devloop.lecturereview.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SaveLectureReviewRequest {
    @NotBlank(message = "후기를 작성해 주세요")
    private String review;
    @NotNull(message = "평점을 작성해주세요")
    private Integer rating;
}
