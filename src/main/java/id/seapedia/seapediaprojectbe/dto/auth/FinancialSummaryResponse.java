package id.seapedia.seapediaprojectbe.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialSummaryResponse {
    // Buyer
    private Long walletBalance;

    // Seller
    private Long sellerIncome;

    // Driver
    private Long driverEarnings;

}
