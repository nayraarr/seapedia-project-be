package id.seapedia.seapediaprojectbe.dto.discount;

import id.seapedia.seapediaprojectbe.model.DiscountValueType;
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
public class VoucherResponse {
    private UUID id;
    private String code;
    private String description;
    private DiscountValueType discountType;
    private Long discountValue;
    private Long maxDiscountAmount;
    private Long minPurchaseAmount;
    private Integer usageLimit;
    private Integer usedCount;
    private Integer remainingUsage;
    private LocalDateTime expiryDate;
    private Boolean active;
    private Boolean expired;
    private LocalDateTime createdAt;
}