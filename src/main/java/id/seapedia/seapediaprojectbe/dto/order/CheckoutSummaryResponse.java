package id.seapedia.seapediaprojectbe.dto.order;

import id.seapedia.seapediaprojectbe.model.DeliveryMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutSummaryResponse {
    private UUID cartId;
    private UUID storeId;
    private String storeName;
    private AddressSnapshotResponse address;
    private DeliveryMethod deliveryMethod;
    private String deliveryMethodLabel;
    private Long subtotal;
    private Long deliveryFee;
    private Integer taxRatePercent;
    private Long taxBase;
    private Long taxAmount;
    private Long totalAmount;
    private Long walletBalance;
    private Boolean walletSufficient;
    private List<CheckoutItemResponse> items;
}
