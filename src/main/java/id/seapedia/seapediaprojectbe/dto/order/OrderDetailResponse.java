package id.seapedia.seapediaprojectbe.dto.order;

import id.seapedia.seapediaprojectbe.model.DeliveryMethod;
import id.seapedia.seapediaprojectbe.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponse {
    private UUID orderId;
    private UUID buyerId;
    private String buyerUsername;
    private UUID storeId;
    private String storeName;
    private AddressSnapshotResponse shippingAddress;
    private DeliveryMethod deliveryMethod;
    private String deliveryMethodLabel;
    private OrderStatus status;
    private String statusLabel;
    private Long subtotal;
    private Long deliveryFee;
    private Integer taxRatePercent;
    private Long taxBase;
    private Long taxAmount;
    private Long totalAmount;
    private Long walletBalanceBefore;
    private Long walletBalanceAfter;
    private List<OrderItemResponse> items;
    private List<OrderStatusHistoryResponse> statusHistory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
