package id.seapedia.seapediaprojectbe.dto.cart;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CartItemResponse {
    private UUID cartItemId;
    private UUID productId;
    private String productName;
    private Long productPrice;
    private Integer quantity;
    private Long subtotal;  // price * quantity
}