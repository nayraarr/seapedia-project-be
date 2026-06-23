package id.seapedia.seapediaprojectbe.dto.admin;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class OverdueProcessResult {
    private UUID orderId;
    private String storeName;
    private String buyerUsername;
    private String deliveryMethod;
    private String previousStatus;
    private String newStatus;
    private Long refundedAmount;
    private boolean stockRestored;
    private String note;
    private boolean skipped;
}