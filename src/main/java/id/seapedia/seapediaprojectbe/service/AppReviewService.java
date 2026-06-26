package id.seapedia.seapediaprojectbe.service;

import id.seapedia.seapediaprojectbe.dto.review.ReviewRequest;
import id.seapedia.seapediaprojectbe.dto.review.ReviewResponse;

import java.util.List;

public interface AppReviewService {
    ReviewResponse createReview(ReviewRequest request);
    List<ReviewResponse> getAllReviews();
}