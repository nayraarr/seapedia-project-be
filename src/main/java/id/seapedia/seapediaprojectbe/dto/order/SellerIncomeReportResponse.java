package id.seapedia.seapediaprojectbe.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerIncomeReportResponse {
    private Integer totalOrders;
    private Integer incomingOrders;
    private Integer processedOrders;
    private Integer cancelledOrders;
    private Long totalIncome;
    private Long totalDiscountGiven;
    private List<StatusCountResponse> statusBreakdown;
    private List<OrderSummaryResponse> recentOrders;
}