package id.seapedia.seapediaprojectbe.controller;

import id.seapedia.seapediaprojectbe.dto.common.ApiResponse;
import id.seapedia.seapediaprojectbe.dto.review.ReviewRequest;
import id.seapedia.seapediaprojectbe.dto.review.ReviewResponse;
import id.seapedia.seapediaprojectbe.security.CustomUserDetails;
import id.seapedia.seapediaprojectbe.service.AppReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
        log.info("[GET /api/reviews]  request received");
        List<ReviewResponse> data = appReviewService.getAllReviews();
        log.info("[GET /api/reviews]  returning {} reviews", data.size());
        return ResponseEntity.ok(ApiResponse.success("Reviews fetched", data));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @RequestBody ReviewRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Rating must be between 1 and 5", null));
        }
        if (request.getComment() == null || request.getComment().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Comment is required", null));
        }
        String displayName;
        if (userDetails != null) {
            displayName = userDetails.getFullName();
            if (displayName == null || displayName.isBlank()) {
                displayName = userDetails.getUsername();
            }
        } else {
            if (request.getReviewerName() == null || request.getReviewerName().isBlank()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Reviewer name is required for guest", null));
            }
            displayName = request.getReviewerName();
        }
        log.info("[POST /api/reviews]  request received: reviewerName={}", displayName);
        ReviewResponse data = appReviewService.createReview(request, displayName);
        log.info("[POST /api/reviews]  review created: id={}", data.getId());
        return ResponseEntity.ok(ApiResponse.success("Review created", data));
    }
}