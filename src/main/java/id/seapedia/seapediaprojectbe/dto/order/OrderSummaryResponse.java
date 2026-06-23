package id.seapedia.seapediaprojectbe.dto.order;

import id.seapedia.seapediaprojectbe.model.DeliveryMethod;
import id.seapedia.seapediaprojectbe.model.DiscountSource;
import id.seapedia.seapediaprojectbe.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryResponse {
    private UUID orderId;
    private UUID storeId;
    private String storeName;
    private UUID buyerId;
    private String buyerUsername;
    private DeliveryMethod deliveryMethod;
    private String deliveryMethodLabel;
    private OrderStatus status;
    private String statusLabel;
    private Long subtotal;
    private String discountCode;
    private DiscountSource discountSource;
    private String discountLabel;
    private Long discountAmount;
    private Long deliveryFee;
    private Integer taxRatePercent;
    private Long taxAmount;
    private Long totalAmount;
    private Integer itemCount;
    private LocalDateTime createdAt;
}
