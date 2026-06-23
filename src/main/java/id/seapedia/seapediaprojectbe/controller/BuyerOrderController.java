package id.seapedia.seapediaprojectbe.controller;

import id.seapedia.seapediaprojectbe.dto.common.ApiResponse;
import id.seapedia.seapediaprojectbe.dto.discount.DiscountValidateRequest;
import id.seapedia.seapediaprojectbe.dto.discount.DiscountValidationResponse;
import id.seapedia.seapediaprojectbe.dto.order.CheckoutRequest;
import id.seapedia.seapediaprojectbe.dto.order.CheckoutSummaryResponse;
import id.seapedia.seapediaprojectbe.dto.order.OrderDetailResponse;
import id.seapedia.seapediaprojectbe.dto.order.OrderSummaryResponse;
import id.seapedia.seapediaprojectbe.security.CustomUserDetails;
import id.seapedia.seapediaprojectbe.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/buyer")
public class BuyerOrderController {

    private final OrderService orderService;

    @PostMapping("/discounts/validate")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ApiResponse<DiscountValidationResponse>> validateDiscountCode(
            @Valid @RequestBody DiscountValidateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        DiscountValidationResponse data =
                orderService.validateDiscountCode(userDetails.getUserId(), request.getCode());
        return ResponseEntity.ok(ApiResponse.success("Discount code validated", data));
    }

    @PostMapping("/checkout/preview")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ApiResponse<CheckoutSummaryResponse>> previewCheckout(
            @Valid @RequestBody CheckoutRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        CheckoutSummaryResponse data = orderService.previewCheckout(userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Checkout summary fetched", data));
    }

    @PostMapping("/checkout")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> createOrder(
            @Valid @RequestBody CheckoutRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        OrderDetailResponse data = orderService.createOrder(userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Order created successfully", data));
    }

    @GetMapping("/orders")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ApiResponse<List<OrderSummaryResponse>>> getHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<OrderSummaryResponse> data = orderService.getBuyerOrderHistory(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Order history fetched", data));
    }

    @GetMapping("/orders/{orderId}")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getDetail(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        OrderDetailResponse data = orderService.getBuyerOrderDetail(userDetails.getUserId(), orderId);
        return ResponseEntity.ok(ApiResponse.success("Order detail fetched", data));
    }
}
