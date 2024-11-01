package com.devloop.lecturereview.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetLectureReviewResponse {
    private String userName;
    private String review;
    private Integer rating;

    private GetLectureReviewResponse(String userName, String review, Integer rating){
        this.userName=userName;
        this.review=review;
        this.rating=rating;
    }

    public static GetLectureReviewResponse of(String userName, String review, Integer rating){
        return new GetLectureReviewResponse(
                userName,
                review,
                rating
        );
    }
}
