package id.seapedia.seapediaprojectbe.dto.wallet;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopUpRequest {

    @NotNull(message = "Amount is required")
    @Min(value = 1000, message = "Minimum top-up amount is 1000")
    private Long amount;
}