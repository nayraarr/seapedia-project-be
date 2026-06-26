package id.seapedia.seapediaprojectbe.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AddToCartRequest {

    @NotNull(message = "productId wajib diisi")
    private UUID productId;

    @NotNull(message = "Quantity wajib diisi")
    @Min(value = 1, message = "Quantity minimal 1")
    private Integer quantity;
}