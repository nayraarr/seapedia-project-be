package id.seapedia.seapediaprojectbe.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCartItemRequest {

    @NotNull(message = "quantity wajib diisi")
    @Min(value = 1, message = "quantity minimal 1")
    private Integer quantity;

}