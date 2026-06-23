package id.seapedia.seapediaprojectbe.dto.order;

import id.seapedia.seapediaprojectbe.model.DeliveryMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {
    private UUID addressId;

    @NotNull(message = "Delivery method is required")
    private DeliveryMethod deliveryMethod;

    private String discountCode;
}
