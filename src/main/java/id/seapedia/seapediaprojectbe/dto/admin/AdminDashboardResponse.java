package id.seapedia.seapediaprojectbe.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AdminDashboardResponse {

    private long totalUsers;
    private long totalAdmins;
    private long totalBuyers;
    private long totalSellers;
    private long totalDrivers;

    private long totalStores;

    private long totalProducts;
    private long outOfStockProducts;
    private long lowStockProducts;

    private long totalOrders;
    private long ordersSedangDikemas;
    private long ordersMenungguPengirim;
    private long ordersDikirim;
    private long ordersSelesai;
    private long ordersDikembalikan;
    private Long totalRevenue;

    private long totalVouchers;
    private long activeVouchers;
    private long expiredVouchers;
    private long totalPromos;
    private long activePromos;
    private long expiredPromos;

    private long totalDeliveryJobs;
    private long unassignedJobs;
    private long ongoingJobs;
    private long completedJobs;

    private long overdueOrders;
    private List<OverdueOrderItem> overdueOrderList;

    private long simulationOffsetMinutes;

    @Data
    @Builder
    public static class OverdueOrderItem {
        private String orderId;
        private String storeName;
        private String buyerUsername;
        private String status;
        private String createdAt;
        private long minutesOverdue;
        private boolean processed;
    }
}