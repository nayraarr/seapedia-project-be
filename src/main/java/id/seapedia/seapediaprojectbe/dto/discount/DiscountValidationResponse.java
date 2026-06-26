package id.seapedia.seapediaprojectbe.dto.discount;

import id.seapedia.seapediaprojectbe.model.DiscountSource;
import id.seapedia.seapediaprojectbe.model.DiscountValueType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountValidationResponse {
    private boolean valid;
    private String code;
    private DiscountSource source;
    private DiscountValueType discountType;
    private Long discountValue;
    private Long discountAmount;
    private String message;
}