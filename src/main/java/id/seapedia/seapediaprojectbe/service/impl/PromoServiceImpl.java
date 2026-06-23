package id.seapedia.seapediaprojectbe.service.impl;

import id.seapedia.seapediaprojectbe.dto.discount.PromoCreateRequest;
import id.seapedia.seapediaprojectbe.dto.discount.PromoResponse;
import id.seapedia.seapediaprojectbe.exception.BadRequestException;
import id.seapedia.seapediaprojectbe.exception.ResourceNotFoundException;
import id.seapedia.seapediaprojectbe.model.DiscountValueType;
import id.seapedia.seapediaprojectbe.model.Promo;
import id.seapedia.seapediaprojectbe.repository.PromoRepository;
import id.seapedia.seapediaprojectbe.repository.VoucherRepository;
import id.seapedia.seapediaprojectbe.service.PromoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromoServiceImpl implements PromoService {

    private final PromoRepository promoRepository;
    private final VoucherRepository voucherRepository;

    private PromoResponse toResponse(Promo promo) {
        return PromoResponse.builder()
                .id(promo.getId())
                .code(promo.getCode())
                .description(promo.getDescription())
                .discountType(promo.getDiscountType())
                .discountValue(promo.getDiscountValue())
                .maxDiscountAmount(promo.getMaxDiscountAmount())
                .minPurchaseAmount(promo.getMinPurchaseAmount())
                .expiryDate(promo.getExpiryDate())
                .active(promo.getActive())
                .expired(promo.isExpired())
                .createdAt(promo.getCreatedAt())
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
    public PromoResponse generatePromo(PromoCreateRequest request) {
        log.info("[generatePromo] code={}", request.getCode());
        String normalizedCode = request.getCode().trim().toUpperCase();

        if (promoRepository.existsByCodeIgnoreCase(normalizedCode)
                || voucherRepository.existsByCodeIgnoreCase(normalizedCode)) {
            throw new BadRequestException("Discount code '" + normalizedCode + "' is already used");
        }

        validateDiscountValue(request.getDiscountType(), request.getDiscountValue());

        Promo promo = Promo.builder()
                .code(normalizedCode)
                .description(request.getDescription())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .maxDiscountAmount(request.getMaxDiscountAmount())
                .minPurchaseAmount(request.getMinPurchaseAmount() != null ? request.getMinPurchaseAmount() : 0L)
                .expiryDate(request.getExpiryDate())
                .active(true)
                .build();

        Promo saved = promoRepository.save(promo);
        log.info("[generatePromo] created promoId={} code={}", saved.getId(), saved.getCode());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromoResponse> listPromos() {
        return promoRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PromoResponse getPromoDetail(UUID id) {
        Promo promo = promoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promo not found"));
        return toResponse(promo);
    }
}