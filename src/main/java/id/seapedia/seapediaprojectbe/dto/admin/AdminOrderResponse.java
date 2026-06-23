package id.seapedia.seapediaprojectbe.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AdminOrderResponse {
    private UUID id;
    private String storeName;
    private String buyerUsername;
    private String status;
    private String statusLabel;
    private Long totalAmount;
    private String deliveryMethod;
    private LocalDateTime createdAt;
}
