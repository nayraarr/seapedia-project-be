package id.seapedia.seapediaprojectbe.controller;

import id.seapedia.seapediaprojectbe.dto.common.ApiResponse;
import id.seapedia.seapediaprojectbe.dto.review.ReviewRequest;
import id.seapedia.seapediaprojectbe.dto.review.ReviewResponse;
import id.seapedia.seapediaprojectbe.service.AppReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class AppReviewController {

    private final AppReviewService appReviewService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getAllReviews() {
        log.info("[GET /api/reviews] 🚀 request received");
        List<ReviewResponse> data = appReviewService.getAllReviews();
        log.info("[GET /api/reviews] ✅ returning {} reviews", data.size());
        return ResponseEntity.ok(ApiResponse.success("Reviews fetched", data));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @Valid @RequestBody ReviewRequest request) {
        log.info("[POST /api/reviews] 🚀 request received: reviewerName={}", request.getReviewerName());
        ReviewResponse data = appReviewService.createReview(request);
        log.info("[POST /api/reviews] ✅ review created: id={}", data.getId());
        return ResponseEntity.ok(ApiResponse.success("Review created", data));
    }
}