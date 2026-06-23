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
public class BuyerSpendingReportResponse {
    private Integer totalOrders;
    private Integer cancelledOrders;
    private Long totalSpent;
    private Long totalDiscountSaved;
    private Long totalDeliveryFee;
    private Long totalTax;
    private List<StatusCountResponse> statusBreakdown;
    private List<OrderSummaryResponse> recentOrders;
}