package id.seapedia.seapediaprojectbe.dto.store;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreRequest {
    @NotBlank(message = "Store name is required")
    private String name;

    private String description;
}
