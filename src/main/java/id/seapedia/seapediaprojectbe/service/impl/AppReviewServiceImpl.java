package id.seapedia.seapediaprojectbe.service.impl;

import id.seapedia.seapediaprojectbe.dto.review.ReviewRequest;
import id.seapedia.seapediaprojectbe.dto.review.ReviewResponse;
import id.seapedia.seapediaprojectbe.model.AppReview;
import id.seapedia.seapediaprojectbe.repository.AppReviewRepository;
import id.seapedia.seapediaprojectbe.service.AppReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppReviewServiceImpl implements AppReviewService {

    private final AppReviewRepository appReviewRepository;

    @Override
    @Transactional
    public ReviewResponse createReview(ReviewRequest request) {
        log.info("[createReview] 🚀 entry: reviewerName={} rating={}", request.getReviewerName(), request.getRating());

        AppReview review = AppReview.builder()
                .reviewerName(request.getReviewerName())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        review = appReviewRepository.save(review);
        log.info("[createReview] ✅ review saved: id={}", review.getId());

        return toResponse(review);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getAllReviews() {
        log.info("[getAllReviews] 🚀 entry");
        List<AppReview> reviews = appReviewRepository.findAllByOrderByCreatedAtDesc();
        log.info("[getAllReviews] ✅ found {} reviews", reviews.size());
        return reviews.stream().map(this::toResponse).toList();
    }

    private ReviewResponse toResponse(AppReview review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .reviewerName(review.getReviewerName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}