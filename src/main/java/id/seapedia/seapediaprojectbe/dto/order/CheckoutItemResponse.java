package id.seapedia.seapediaprojectbe.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutItemResponse {
    private UUID productId;
    private String productName;
    private Long unitPrice;
    private Integer quantity;
    private Long subtotal;
}
