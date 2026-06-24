package id.seapedia.seapediaprojectbe.service;

import id.seapedia.seapediaprojectbe.dto.admin.AdminDashboardResponse;
import id.seapedia.seapediaprojectbe.dto.admin.AdminDeliveryJobResponse;
import id.seapedia.seapediaprojectbe.dto.admin.AdminOrderResponse;
import id.seapedia.seapediaprojectbe.dto.admin.AdminUserResponse;
import id.seapedia.seapediaprojectbe.dto.order.OrderDetailResponse;

import java.util.List;
import java.util.UUID;

public interface AdminService {
    AdminDashboardResponse getDashboardSummary();
    List<AdminUserResponse> listUsers();
    List<AdminOrderResponse> listOrders();
    List<AdminDeliveryJobResponse> listDeliveryJobs();
    OrderDetailResponse getOrderDetail(UUID orderId);
}