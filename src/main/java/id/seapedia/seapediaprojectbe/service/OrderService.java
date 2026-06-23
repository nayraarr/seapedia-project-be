package id.seapedia.seapediaprojectbe.service;

import id.seapedia.seapediaprojectbe.dto.discount.DiscountValidationResponse;
import id.seapedia.seapediaprojectbe.dto.order.CheckoutRequest;
import id.seapedia.seapediaprojectbe.dto.order.CheckoutSummaryResponse;
import id.seapedia.seapediaprojectbe.dto.order.OrderDetailResponse;
import id.seapedia.seapediaprojectbe.dto.order.OrderSummaryResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    CheckoutSummaryResponse previewCheckout(UUID buyerId, CheckoutRequest request);
    OrderDetailResponse createOrder(UUID buyerId, CheckoutRequest request);
    List<OrderSummaryResponse> getBuyerOrderHistory(UUID buyerId);
    OrderDetailResponse getBuyerOrderDetail(UUID buyerId, UUID orderId);
    List<OrderSummaryResponse> getSellerIncomingOrders(UUID sellerId);
    OrderDetailResponse getSellerOrderDetail(UUID sellerId, UUID orderId);

    @Transactional(readOnly = true)
    DiscountValidationResponse validateDiscountCode(UUID buyerId, String code);
}
