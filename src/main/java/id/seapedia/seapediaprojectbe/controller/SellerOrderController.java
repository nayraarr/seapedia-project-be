package id.seapedia.seapediaprojectbe.controller;

import id.seapedia.seapediaprojectbe.dto.common.ApiResponse;
import id.seapedia.seapediaprojectbe.dto.order.OrderDetailResponse;
import id.seapedia.seapediaprojectbe.dto.order.OrderSummaryResponse;
import id.seapedia.seapediaprojectbe.security.CustomUserDetails;
import id.seapedia.seapediaprojectbe.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller/orders")
public class SellerOrderController {

    private final OrderService orderService;

    @GetMapping("/incoming")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<List<OrderSummaryResponse>>> getIncomingOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<OrderSummaryResponse> data = orderService.getSellerIncomingOrders(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Incoming orders fetched", data));
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getIncomingOrderDetail(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        OrderDetailResponse data = orderService.getSellerOrderDetail(userDetails.getUserId(), orderId);
        return ResponseEntity.ok(ApiResponse.success("Order detail fetched", data));
    }
}
