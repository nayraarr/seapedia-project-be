package id.seapedia.seapediaprojectbe.dto.delivery;

import id.seapedia.seapediaprojectbe.model.DeliveryMethod;
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
public class DeliveryJobSummaryResponse {
    private UUID deliveryJobId;
    private UUID orderId;
    private UUID storeId;
    private String storeName;
    private String recipientName;
    private String city;
    private String postalCode;
    private DeliveryMethod deliveryMethod;
    private String deliveryMethodLabel;
    private Integer itemCount;
    private Long totalAmount;
    private LocalDateTime availableSince;
}