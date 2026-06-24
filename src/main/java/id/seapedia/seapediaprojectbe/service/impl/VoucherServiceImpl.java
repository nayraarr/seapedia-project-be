package id.seapedia.seapediaprojectbe.service.impl;

import id.seapedia.seapediaprojectbe.dto.discount.VoucherCreateRequest;
import id.seapedia.seapediaprojectbe.dto.discount.VoucherResponse;
import id.seapedia.seapediaprojectbe.exception.BadRequestException;
import id.seapedia.seapediaprojectbe.exception.ResourceNotFoundException;
import id.seapedia.seapediaprojectbe.model.DiscountValueType;
import id.seapedia.seapediaprojectbe.model.Voucher;
import id.seapedia.seapediaprojectbe.repository.PromoRepository;
import id.seapedia.seapediaprojectbe.repository.VoucherRepository;
import id.seapedia.seapediaprojectbe.service.VoucherService;
import id.seapedia.seapediaprojectbe.util.SanitizerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final PromoRepository promoRepository;

    private VoucherResponse toResponse(Voucher voucher) {
        return VoucherResponse.builder()
                .id(voucher.getId())
                .code(voucher.getCode())
                .description(voucher.getDescription())
                .discountType(voucher.getDiscountType())
                .discountValue(voucher.getDiscountValue())
                .maxDiscountAmount(voucher.getMaxDiscountAmount())
                .minPurchaseAmount(voucher.getMinPurchaseAmount())
                .usageLimit(voucher.getUsageLimit())
                .usedCount(voucher.getUsedCount())
                .remainingUsage(voucher.getRemainingUsage())
                .expiryDate(voucher.getExpiryDate())
                .active(voucher.getActive())
                .expired(voucher.isExpired())
                .createdAt(voucher.getCreatedAt())
                .build();
    }

    private void validateDiscountValue(DiscountValueType type, Long value) {
        if (type == DiscountValueType.PERCENTAGE && (value <= 0 || value > 100)) {
            throw new BadRequestException("Percentage discount value must be between 1 and 100");
        }
        if (type == DiscountValueType.FIXED && value <= 0) {
            throw new BadRequestException("Fixed discount value must be greater than 0");
        }
    }

    @Override
    @Transactional
    public VoucherResponse generateVoucher(VoucherCreateRequest request) {
        log.info("[generateVoucher] code={}", request.getCode());
        String normalizedCode = request.getCode().trim().toUpperCase();

        if (voucherRepository.existsByCodeIgnoreCase(normalizedCode)
                || promoRepository.existsByCodeIgnoreCase(normalizedCode)) {
            throw new BadRequestException("Discount code '" + normalizedCode + "' is already used");
        }

        validateDiscountValue(request.getDiscountType(), request.getDiscountValue());

        Voucher voucher = Voucher.builder()
                .code(normalizedCode)
                .description(SanitizerUtil.clean(request.getDescription()))
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .maxDiscountAmount(request.getMaxDiscountAmount())
                .minPurchaseAmount(request.getMinPurchaseAmount() != null ? request.getMinPurchaseAmount() : 0L)
                .usageLimit(request.getUsageLimit())
                .usedCount(0)
                .expiryDate(request.getExpiryDate())
                .active(true)
                .build();

        Voucher saved = voucherRepository.save(voucher);
        log.info("[generateVoucher] created voucherId={} code={}", saved.getId(), saved.getCode());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoucherResponse> listVouchers() {
        return voucherRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .filter(v -> v.hasRemainingUsage())
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoucherResponse> listAllVouchers() {
        return voucherRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VoucherResponse getVoucherDetail(UUID id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found"));
        return toResponse(voucher);
    }
}