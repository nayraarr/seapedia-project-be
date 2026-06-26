package id.seapedia.seapediaprojectbe.dto.store;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreRequest {
    @NotBlank(message = "Store name is required")
    @Size(max = 50, message = "Store name max 50 characters")
    private String name;

    private String description;
}
