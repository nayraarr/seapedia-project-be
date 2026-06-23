package id.seapedia.seapediaprojectbe.dto.cart;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CartSummaryResponse {
    private UUID cartId;
    private UUID storeId;
    private String storeName;
    private List<CartItemResponse> items;
    private int totalItems;
    private Long grandTotal;
}