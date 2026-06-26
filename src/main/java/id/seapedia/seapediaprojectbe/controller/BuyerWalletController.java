package id.seapedia.seapediaprojectbe.controller;

import id.seapedia.seapediaprojectbe.dto.common.ApiResponse;
import id.seapedia.seapediaprojectbe.dto.wallet.TopUpRequest;
import id.seapedia.seapediaprojectbe.dto.wallet.WalletResponse;
import id.seapedia.seapediaprojectbe.dto.wallet.WalletTransactionResponse;
import id.seapedia.seapediaprojectbe.security.CustomUserDetails;
import id.seapedia.seapediaprojectbe.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/buyer/wallet")
@RequiredArgsConstructor
public class BuyerWalletController {

    private final WalletService walletService;

    @GetMapping
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ApiResponse<WalletResponse>> getWallet(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("[GET /buyer/wallet]  userId={}", userDetails.getUserId());
        WalletResponse data = walletService.getWallet(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Wallet fetched", data));
    }

    @PostMapping("/topup")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ApiResponse<WalletResponse>> topUp(
            @Valid @RequestBody TopUpRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("[POST /buyer/wallet/topup]  userId={} amount={}", userDetails.getUserId(), request.getAmount());
        WalletResponse data = walletService.topUp(userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Top up successful", data));
    }

    @GetMapping("/transactions")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ApiResponse<List<WalletTransactionResponse>>> getTransactions(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("[GET /buyer/wallet/transactions]  userId={}", userDetails.getUserId());
        List<WalletTransactionResponse> data = walletService.getTransactionHistory(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Transactions fetched", data));
    }
}