package id.seapedia.seapediaprojectbe.controller;

import id.seapedia.seapediaprojectbe.dto.common.ApiResponse;
import id.seapedia.seapediaprojectbe.dto.discount.PromoCreateRequest;
import id.seapedia.seapediaprojectbe.dto.discount.PromoResponse;
import id.seapedia.seapediaprojectbe.dto.discount.VoucherCreateRequest;
import id.seapedia.seapediaprojectbe.dto.discount.VoucherResponse;
import id.seapedia.seapediaprojectbe.service.PromoService;
import id.seapedia.seapediaprojectbe.service.VoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminDiscountController {

    private final VoucherService voucherService;
    private final PromoService promoService;

    @PostMapping("/vouchers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VoucherResponse>> generateVoucher(
            @Valid @RequestBody VoucherCreateRequest request) {
        VoucherResponse data = voucherService.generateVoucher(request);
        return ResponseEntity.ok(ApiResponse.success("Voucher generated successfully", data));
    }

    @PostMapping("/promos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PromoResponse>> generatePromo(
            @Valid @RequestBody PromoCreateRequest request) {
        PromoResponse data = promoService.generatePromo(request);
        return ResponseEntity.ok(ApiResponse.success("Promo generated successfully", data));
    }
}