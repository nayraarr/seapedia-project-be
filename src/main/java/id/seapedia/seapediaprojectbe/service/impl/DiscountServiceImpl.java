package id.seapedia.seapediaprojectbe.service.impl;

import id.seapedia.seapediaprojectbe.dto.discount.DiscountValidationResponse;
import id.seapedia.seapediaprojectbe.exception.BadRequestException;
import id.seapedia.seapediaprojectbe.exception.ResourceNotFoundException;
import id.seapedia.seapediaprojectbe.model.DiscountSource;
import id.seapedia.seapediaprojectbe.model.DiscountValueType;
import id.seapedia.seapediaprojectbe.model.Promo;
import id.seapedia.seapediaprojectbe.model.Voucher;
import id.seapedia.seapediaprojectbe.repository.PromoRepository;
import id.seapedia.seapediaprojectbe.repository.VoucherRepository;
import id.seapedia.seapediaprojectbe.service.DiscountResolution;
import id.seapedia.seapediaprojectbe.service.DiscountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {

    private final VoucherRepository voucherRepository;
    private final PromoRepository promoRepository;

    private long calculateDiscountAmount(DiscountValueType type, long value, Long maxDiscountAmount, long subtotal) {
        long rawAmount = (type == DiscountValueType.PERCENTAGE)
                ? Math.round(subtotal * value / 100.0)
                : value;

        if (maxDiscountAmount != null && maxDiscountAmount > 0) {
            rawAmount = Math.min(rawAmount, maxDiscountAmount);
        }

        return Math.min(rawAmount, subtotal);
    }

    private DiscountResolution resolveVoucher(Voucher voucher, long subtotal) {
        if (!Boolean.TRUE.equals(voucher.getActive())) {
            throw new BadRequestException("Voucher '" + voucher.getCode() + "' is not active");
        }
        if (voucher.isExpired()) {
            throw new BadRequestException("Voucher '" + voucher.getCode() + "' has expired");
        }
        if (!voucher.hasRemainingUsage()) {
            throw new BadRequestException("Voucher '" + voucher.getCode() + "' has no remaining usage");
        }
        if (subtotal < voucher.getMinPurchaseAmount()) {
            throw new BadRequestException(
                    "Minimum purchase for voucher '" + voucher.getCode() + "' is " + voucher.getMinPurchaseAmount());
        }

        long discountAmount = calculateDiscountAmount(
                voucher.getDiscountType(), voucher.getDiscountValue(), voucher.getMaxDiscountAmount(), subtotal);

        return new DiscountResolution(
                DiscountSource.VOUCHER,
                voucher.getId(),
                voucher.getCode(),
                voucher.getDiscountType(),
                voucher.getDiscountValue(),
                discountAmount,
                voucher.getDescription()
        );
    }

    private DiscountResolution resolvePromo(Promo promo, long subtotal) {
        if (!Boolean.TRUE.equals(promo.getActive())) {
            throw new BadRequestException("Promo '" + promo.getCode() + "' is not active");
        }
        if (promo.isExpired()) {
            throw new BadRequestException("Promo '" + promo.getCode() + "' has expired");
        }
        if (subtotal < promo.getMinPurchaseAmount()) {
            throw new BadRequestException(
                    "Minimum purchase for promo '" + promo.getCode() + "' is " + promo.getMinPurchaseAmount());
        }

        long discountAmount = calculateDiscountAmount(
                promo.getDiscountType(), promo.getDiscountValue(), promo.getMaxDiscountAmount(), subtotal);

        return new DiscountResolution(
                DiscountSource.PROMO,
                promo.getId(),
                promo.getCode(),
                promo.getDiscountType(),
                promo.getDiscountValue(),
                discountAmount,
                promo.getDescription()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DiscountResolution resolve(String code, long subtotal) {
        if (code == null || code.isBlank()) {
            return DiscountResolution.none();
        }
        String normalizedCode = code.trim().toUpperCase();

        var voucher = voucherRepository.findByCodeIgnoreCase(normalizedCode);
        if (voucher.isPresent()) {
            return resolveVoucher(voucher.get(), subtotal);
        }

        var promo = promoRepository.findByCodeIgnoreCase(normalizedCode);
        if (promo.isPresent()) {
            return resolvePromo(promo.get(), subtotal);
        }

        throw new ResourceNotFoundException("Discount code '" + normalizedCode + "' not found");
    }

    @Override
    @Transactional(readOnly = true)
    public DiscountValidationResponse validate(String code, long subtotal) {
        try {
            DiscountResolution resolution = resolve(code, subtotal);
            if (resolution.source() == DiscountSource.NONE) {
                return DiscountValidationResponse.builder()
                        .valid(false)
                        .code(code)
                        .source(DiscountSource.NONE)
                        .message("No discount code provided")
                        .build();
            }
            return DiscountValidationResponse.builder()
                    .valid(true)
                    .code(resolution.code())
                    .source(resolution.source())
                    .discountType(resolution.discountType())
                    .discountValue(resolution.discountValue())
                    .discountAmount(resolution.discountAmount())
                    .message(resolution.source() == DiscountSource.VOUCHER
                            ? "Voucher is valid and can be applied"
                            : "Promo is valid and can be applied")
                    .build();
        } catch (BadRequestException | ResourceNotFoundException ex) {
            return DiscountValidationResponse.builder()
                    .valid(false)
                    .code(code)
                    .source(DiscountSource.NONE)
                    .message(ex.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public void consume(DiscountResolution resolution) {
        if (resolution == null || resolution.source() != DiscountSource.VOUCHER) {
            return;
        }

        Voucher voucher = voucherRepository.findByIdForUpdate(resolution.discountId())
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found"));

        if (voucher.isExpired()) {
            throw new BadRequestException("Voucher '" + voucher.getCode() + "' has expired");
        }
        if (!voucher.hasRemainingUsage()) {
            throw new BadRequestException("Voucher '" + voucher.getCode() + "' has no remaining usage");
        }

        voucher.setUsedCount(voucher.getUsedCount() + 1);
        voucherRepository.save(voucher);
        log.info("[consume] voucherId={} code={} usedCount={}/{}",
                voucher.getId(), voucher.getCode(), voucher.getUsedCount(), voucher.getUsageLimit());
    }
}