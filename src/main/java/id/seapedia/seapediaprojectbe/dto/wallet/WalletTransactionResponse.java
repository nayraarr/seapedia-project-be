package id.seapedia.seapediaprojectbe.dto.wallet;

import id.seapedia.seapediaprojectbe.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransactionResponse {
    private UUID id;
    private TransactionType type;
    private Long amount;
    private Long balanceAfter;
    private String description;
    private LocalDateTime createdAt;
}