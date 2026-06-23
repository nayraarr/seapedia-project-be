package id.seapedia.seapediaprojectbe.controller;

import id.seapedia.seapediaprojectbe.dto.admin.AdminDashboardResponse;
import id.seapedia.seapediaprojectbe.dto.admin.AdminDeliveryJobResponse;
import id.seapedia.seapediaprojectbe.dto.admin.AdminOrderResponse;
import id.seapedia.seapediaprojectbe.dto.admin.AdminUserResponse;
import id.seapedia.seapediaprojectbe.dto.common.ApiResponse;
import id.seapedia.seapediaprojectbe.security.CustomUserDetails;
import id.seapedia.seapediaprojectbe.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getDashboard(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("[GET /api/admin/dashboard] userId={}", userDetails.getUserId());
        AdminDashboardResponse data = adminService.getDashboardSummary();
        return ResponseEntity.ok(ApiResponse.success("Admin dashboard", data));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> getUsers() {
        List<AdminUserResponse> data = adminService.listUsers();
        return ResponseEntity.ok(ApiResponse.success("Users list", data));
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<AdminOrderResponse>>> getOrders() {
        List<AdminOrderResponse> data = adminService.listOrders();
        return ResponseEntity.ok(ApiResponse.success("Orders list", data));
    }

    @GetMapping("/delivery-jobs")
    public ResponseEntity<ApiResponse<List<AdminDeliveryJobResponse>>> getDeliveryJobs() {
        List<AdminDeliveryJobResponse> data = adminService.listDeliveryJobs();
        return ResponseEntity.ok(ApiResponse.success("Delivery jobs list", data));
    }
}