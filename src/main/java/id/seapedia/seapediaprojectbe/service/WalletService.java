package id.seapedia.seapediaprojectbe.service;

import id.seapedia.seapediaprojectbe.dto.wallet.TopUpRequest;
import id.seapedia.seapediaprojectbe.dto.wallet.WalletResponse;
import id.seapedia.seapediaprojectbe.dto.wallet.WalletTransactionResponse;

import java.util.List;
import java.util.UUID;

public interface WalletService {
    WalletResponse getWallet(UUID userId);
    WalletResponse topUp(UUID userId, TopUpRequest request);
    List<WalletTransactionResponse> getTransactionHistory(UUID userId);

    void deductBalance(UUID userId, Long amount, String description);
}