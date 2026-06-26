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

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminDiscountController {

    private final VoucherService voucherService;
    private final PromoService promoService;

    @GetMapping("/vouchers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<VoucherResponse>>> listAllVouchers() {
        List<VoucherResponse> data = voucherService.listAllVouchers();
        return ResponseEntity.ok(ApiResponse.success("All vouchers fetched", data));
    }

    @GetMapping("/promos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PromoResponse>>> listAllPromos() {
        List<PromoResponse> data = promoService.listPromos();
        return ResponseEntity.ok(ApiResponse.success("All promos fetched", data));
    }

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