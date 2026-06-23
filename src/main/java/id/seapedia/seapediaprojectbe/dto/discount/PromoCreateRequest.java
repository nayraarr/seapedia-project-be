package id.seapedia.seapediaprojectbe.dto.discount;

import id.seapedia.seapediaprojectbe.model.DiscountValueType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoCreateRequest {

    @NotBlank(message = "Code is required")
    private String code;

    private String description;

    @NotNull(message = "Discount type is required")
    private DiscountValueType discountType;

    @NotNull(message = "Discount value is required")
    @Positive(message = "Discount value must be positive")
    private Long discountValue;

    private Long maxDiscountAmount;

    @PositiveOrZero(message = "Min purchase amount cannot be negative")
    private Long minPurchaseAmount;

    @NotNull(message = "Expiry date is required")
    @Future(message = "Expiry date must be in the future")
    private LocalDateTime expiryDate;
}