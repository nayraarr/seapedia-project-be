package id.seapedia.seapediaprojectbe.service;

import id.seapedia.seapediaprojectbe.dto.delivery.DeliveryJobDetailResponse;
import id.seapedia.seapediaprojectbe.dto.delivery.DeliveryJobSummaryResponse;
import id.seapedia.seapediaprojectbe.dto.delivery.DriverIncomeReportResponse;

import java.util.List;
import java.util.UUID;

public interface DeliveryService {
    List<DeliveryJobSummaryResponse> getAvailableJobs();
    DeliveryJobDetailResponse getJobDetail(UUID jobId);
    DeliveryJobDetailResponse takeJob(UUID jobId, UUID driverId);
    List<DeliveryJobSummaryResponse> getActiveJobs(UUID driverId);
    List<DeliveryJobSummaryResponse> getJobHistory(UUID driverId);
    DriverIncomeReportResponse getDriverReport(UUID driverId);
    DeliveryJobDetailResponse completeJob(UUID jobId, UUID driverId);
}