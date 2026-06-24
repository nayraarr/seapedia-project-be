package id.seapedia.seapediaprojectbe.service.impl;

import id.seapedia.seapediaprojectbe.dto.wallet.TopUpRequest;
import id.seapedia.seapediaprojectbe.dto.wallet.WalletResponse;
import id.seapedia.seapediaprojectbe.dto.wallet.WalletTransactionResponse;
import id.seapedia.seapediaprojectbe.exception.BadRequestException;
import id.seapedia.seapediaprojectbe.model.TransactionType;
import id.seapedia.seapediaprojectbe.model.Wallet;
import id.seapedia.seapediaprojectbe.model.WalletTransaction;
import id.seapedia.seapediaprojectbe.repository.WalletRepository;
import id.seapedia.seapediaprojectbe.repository.WalletTransactionRepository;
import id.seapedia.seapediaprojectbe.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    private Wallet getOrCreateWallet(UUID userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> {
                    log.info("[getOrCreateWallet] 🆕 creating wallet for userId={}", userId);
                    Wallet w = Wallet.builder().userId(userId).balance(0L).build();
                    return walletRepository.save(w);
                });
    }

    private Wallet getOrCreateWalletForUpdate(UUID userId) {
        return walletRepository.findByUserIdForUpdate(userId)
                .orElseGet(() -> {
                    log.info("[getOrCreateWalletForUpdate] creating wallet for userId={}", userId);
                    Wallet w = Wallet.builder().userId(userId).balance(0L).build();
                    return walletRepository.save(w);
                });
    }

    private WalletResponse toResponse(Wallet wallet) {
        return WalletResponse.builder()
                .id(wallet.getId())
                .balance(wallet.getBalance())
                .updatedAt(wallet.getUpdatedAt())
                .build();
    }

    private WalletTransactionResponse toTxResponse(WalletTransaction tx) {
        return WalletTransactionResponse.builder()
                .id(tx.getId())
                .type(tx.getType())
                .amount(tx.getAmount())
                .balanceAfter(tx.getBalanceAfter())
                .description(tx.getDescription())
                .createdAt(tx.getCreatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public WalletResponse getWallet(UUID userId) {
        log.info("[getWallet] 🚀 entry: userId={}", userId);
        Wallet wallet = getOrCreateWallet(userId);
        log.info("[getWallet] ✅ balance={}", wallet.getBalance());
        return toResponse(wallet);
    }

    @Override
    @Transactional
    public WalletResponse topUp(UUID userId, TopUpRequest request) {
        log.info("[topUp] 🚀 entry: userId={} amount={}", userId, request.getAmount());

        Wallet wallet = getOrCreateWalletForUpdate(userId);
        wallet.setBalance(wallet.getBalance() + request.getAmount());
        wallet = walletRepository.save(wallet);

        WalletTransaction tx = WalletTransaction.builder()
                .wallet(wallet)
                .type(TransactionType.TOPUP)
                .amount(request.getAmount())
                .balanceAfter(wallet.getBalance())
                .description("Top up saldo")
                .build();
        walletTransactionRepository.save(tx);

        log.info("[topUp] ✅ new balance={} walletId={}", wallet.getBalance(), wallet.getId());
        return toResponse(wallet);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalletTransactionResponse> getTransactionHistory(UUID userId) {
        log.info("[getTransactionHistory] 🚀 entry: userId={}", userId);
        Wallet wallet = getOrCreateWallet(userId);
        List<WalletTransaction> txList =
                walletTransactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId());
        log.info("[getTransactionHistory] ✅ found {} transactions", txList.size());
        return txList.stream().map(this::toTxResponse).toList();
    }

    @Override
    @Transactional
    public void deductBalance(UUID userId, Long amount, String description) {
        log.info("[deductBalance] 🚀 entry: userId={} amount={}", userId, amount);
        Wallet wallet = getOrCreateWalletForUpdate(userId);

        if (wallet.getBalance() < amount) {
            log.warn("[deductBalance] ⚠️ insufficient balance: balance={} needed={}",
                    wallet.getBalance(), amount);
            throw new BadRequestException("Insufficient wallet balance");
        }

        wallet.setBalance(wallet.getBalance() - amount);
        wallet = walletRepository.save(wallet);

        WalletTransaction tx = WalletTransaction.builder()
                .wallet(wallet)
                .type(TransactionType.PAYMENT)
                .amount(amount)
                .balanceAfter(wallet.getBalance())
                .description(description)
                .build();
        walletTransactionRepository.save(tx);
        log.info("[deductBalance] ✅ deducted, new balance={}", wallet.getBalance());
    }

    @Override
    @Transactional
    public void refundBalance(UUID userId, Long amount, String description) {
        log.info("[refundBalance] userId={} amount={}", userId, amount);
        Wallet wallet = getOrCreateWalletForUpdate(userId);

        wallet.setBalance(wallet.getBalance() + amount);
        wallet = walletRepository.save(wallet);

        WalletTransaction tx = WalletTransaction.builder()
                .wallet(wallet)
                .type(TransactionType.REFUND)
                .amount(amount)
                .balanceAfter(wallet.getBalance())
                .description(description)
                .build();
        walletTransactionRepository.save(tx);
        log.info("[refundBalance] refunded, new balance={}", wallet.getBalance());
    }

    @Override
    @Transactional
    public void creditBalance(UUID userId, Long amount, String description) {
        log.info("[creditBalance] userId={} amount={}", userId, amount);
        Wallet wallet = getOrCreateWalletForUpdate(userId);

        wallet.setBalance(wallet.getBalance() + amount);
        wallet = walletRepository.save(wallet);

        WalletTransaction tx = WalletTransaction.builder()
                .wallet(wallet)
                .type(TransactionType.REVENUE)
                .amount(amount)
                .balanceAfter(wallet.getBalance())
                .description(description)
                .build();
        walletTransactionRepository.save(tx);
        log.info("[creditBalance] credited, new balance={}", wallet.getBalance());
    }
}
