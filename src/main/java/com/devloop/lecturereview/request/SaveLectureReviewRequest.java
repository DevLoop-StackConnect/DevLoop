package com.devloop.lecturereview.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SaveLectureReviewRequest {
    @NotBlank(message = "후기를 작성해 주세요")
    private String review;
    @NotNull(message = "평점을 작성해주세요")
    private Integer rating;
}
