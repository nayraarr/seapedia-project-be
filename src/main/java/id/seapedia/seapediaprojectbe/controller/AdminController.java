package id.seapedia.seapediaprojectbe.controller;

import id.seapedia.seapediaprojectbe.dto.admin.*;
import id.seapedia.seapediaprojectbe.dto.common.ApiResponse;
import id.seapedia.seapediaprojectbe.security.CustomUserDetails;
import id.seapedia.seapediaprojectbe.service.AdminService;
import id.seapedia.seapediaprojectbe.service.OverdueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final OverdueService overdueService;

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

    @PostMapping("/overdue/process")
    public ResponseEntity<ApiResponse<List<OverdueProcessResult>>> processOverdue() {
        log.info("[POST /api/admin/overdue/process]");
        List<OverdueProcessResult> results = overdueService.processAllOverdueOrders();
        return ResponseEntity.ok(ApiResponse.success("Overdue orders processed", results));
    }

    @PostMapping("/overdue/process/{orderId}")
    public ResponseEntity<ApiResponse<OverdueProcessResult>> processOneOverdue(
            @PathVariable UUID orderId) {
        log.info("[POST /api/admin/overdue/process/{}]", orderId);
        OverdueProcessResult result = overdueService.processOverdueOrder(orderId);
        return ResponseEntity.ok(ApiResponse.success("Overdue order processed", result));
    }
}