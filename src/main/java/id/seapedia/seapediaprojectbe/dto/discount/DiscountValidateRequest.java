package id.seapedia.seapediaprojectbe.dto.discount;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountValidateRequest {
    @NotBlank(message = "Discount code is required")
    private String code;
}