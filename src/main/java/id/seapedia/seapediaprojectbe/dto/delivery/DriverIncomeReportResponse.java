package id.seapedia.seapediaprojectbe.dto.delivery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverIncomeReportResponse {
    private Integer totalJobsTaken;
    private Integer completedJobs;
    private Integer activeJobs;
    private Long totalIncome;
    private Long totalDeliveryFee;
    private List<DeliveryJobSummaryResponse> recentCompletedJobs;
}
