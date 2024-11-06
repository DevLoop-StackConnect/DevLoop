package com.devloop.lecturereview.response;

import lombok.Getter;

@Getter
public class GetLectureReviewResponse {
    private final String userName;
    private final String review;
    private final Integer rating;

    private GetLectureReviewResponse(String userName, String review, Integer rating) {
        this.userName = userName;
        this.review = review;
        this.rating = rating;
    }

    public static GetLectureReviewResponse of(String userName, String review, Integer rating) {
        return new GetLectureReviewResponse(
                userName,
                review,
                rating
        );
    }
}
