package id.seapedia.seapediaprojectbe.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AdminDeliveryJobResponse {
    private UUID id;
    private UUID orderId;
    private String storeName;
    private UUID driverId;
    private String orderStatus;
    private String orderStatusLabel;
    private LocalDateTime createdAt;
}
