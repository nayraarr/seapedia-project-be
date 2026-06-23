package id.seapedia.seapediaprojectbe.controller;

import id.seapedia.seapediaprojectbe.dto.common.ApiResponse;
import id.seapedia.seapediaprojectbe.dto.delivery.DeliveryJobDetailResponse;
import id.seapedia.seapediaprojectbe.dto.delivery.DeliveryJobSummaryResponse;
import id.seapedia.seapediaprojectbe.dto.delivery.DriverIncomeReportResponse;
import id.seapedia.seapediaprojectbe.security.CustomUserDetails;
import id.seapedia.seapediaprojectbe.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/driver/jobs")
public class DriverDeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<ApiResponse<List<DeliveryJobSummaryResponse>>> getAvailableJobs() {
        List<DeliveryJobSummaryResponse> data = deliveryService.getAvailableJobs();
        return ResponseEntity.ok(ApiResponse.success("Available delivery jobs fetched", data));
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<ApiResponse<List<DeliveryJobSummaryResponse>>> getActiveJobs(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<DeliveryJobSummaryResponse> data = deliveryService.getActiveJobs(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Active delivery jobs fetched", data));
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<ApiResponse<List<DeliveryJobSummaryResponse>>> getJobHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<DeliveryJobSummaryResponse> data = deliveryService.getJobHistory(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Delivery job history fetched", data));
    }

    @GetMapping("/report")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<ApiResponse<DriverIncomeReportResponse>> getReport(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        DriverIncomeReportResponse data = deliveryService.getDriverReport(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Driver income report fetched", data));
    }

    @GetMapping("/{jobId}")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<ApiResponse<DeliveryJobDetailResponse>> getJobDetail(
            @PathVariable UUID jobId) {
        DeliveryJobDetailResponse data = deliveryService.getJobDetail(jobId);
        return ResponseEntity.ok(ApiResponse.success("Delivery job detail fetched", data));
    }

    @PatchMapping("/{jobId}/take")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<ApiResponse<DeliveryJobDetailResponse>> takeJob(
            @PathVariable UUID jobId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        DeliveryJobDetailResponse data = deliveryService.takeJob(jobId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Delivery job taken successfully", data));
    }

    @PatchMapping("/{jobId}/complete")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<ApiResponse<DeliveryJobDetailResponse>> completeJob(
            @PathVariable UUID jobId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        DeliveryJobDetailResponse data = deliveryService.completeJob(jobId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Delivery completed successfully", data));
    }
}