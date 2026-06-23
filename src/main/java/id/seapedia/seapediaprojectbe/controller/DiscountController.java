package id.seapedia.seapediaprojectbe.controller;

import id.seapedia.seapediaprojectbe.dto.common.ApiResponse;
import id.seapedia.seapediaprojectbe.dto.discount.PromoResponse;
import id.seapedia.seapediaprojectbe.dto.discount.VoucherResponse;
import id.seapedia.seapediaprojectbe.service.PromoService;
import id.seapedia.seapediaprojectbe.service.VoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/discounts")
@RequiredArgsConstructor
public class DiscountController {

    private final VoucherService voucherService;
    private final PromoService promoService;

    @GetMapping("/vouchers")
    public ResponseEntity<ApiResponse<List<VoucherResponse>>> listVouchers() {
        List<VoucherResponse> data = voucherService.listVouchers();
        return ResponseEntity.ok(ApiResponse.success("Vouchers fetched", data));
    }

    @GetMapping("/vouchers/{id}")
    public ResponseEntity<ApiResponse<VoucherResponse>> getVoucherDetail(@PathVariable UUID id) {
        VoucherResponse data = voucherService.getVoucherDetail(id);
        return ResponseEntity.ok(ApiResponse.success("Voucher detail fetched", data));
    }

    @GetMapping("/promos")
    public ResponseEntity<ApiResponse<List<PromoResponse>>> listPromos() {
        List<PromoResponse> data = promoService.listPromos();
        return ResponseEntity.ok(ApiResponse.success("Promos fetched", data));
    }

    @GetMapping("/promos/{id}")
    public ResponseEntity<ApiResponse<PromoResponse>> getPromoDetail(@PathVariable UUID id) {
        PromoResponse data = promoService.getPromoDetail(id);
        return ResponseEntity.ok(ApiResponse.success("Promo detail fetched", data));
    }
}