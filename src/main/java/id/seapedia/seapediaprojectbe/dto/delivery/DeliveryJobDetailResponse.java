package id.seapedia.seapediaprojectbe.dto.delivery;

import id.seapedia.seapediaprojectbe.dto.order.AddressSnapshotResponse;
import id.seapedia.seapediaprojectbe.dto.order.OrderItemResponse;
import id.seapedia.seapediaprojectbe.dto.order.OrderStatusHistoryResponse;
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
public class DeliveryJobDetailResponse {
    private UUID deliveryJobId;
    private UUID orderId;
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
    private Long taxAmount;
    private Long totalAmount;
    private List<OrderItemResponse> items;
    private List<OrderStatusHistoryResponse> statusHistory;
    private LocalDateTime availableSince;
}