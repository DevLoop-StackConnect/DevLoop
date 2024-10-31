package com.devloop.lecturereview.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LectureReviewResponse {
    private String userName;
    private String review;
    private Integer rating;

    private LectureReviewResponse(String userName,String review,Integer rating){
        this.userName=userName;
        this.review=review;
        this.rating=rating;
    }

    public static LectureReviewResponse of(String userName,String review,Integer rating){
        return new LectureReviewResponse(
                userName,
                review,
                rating
        );
    }
}
