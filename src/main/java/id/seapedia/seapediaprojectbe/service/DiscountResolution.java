package id.seapedia.seapediaprojectbe.service;

import id.seapedia.seapediaprojectbe.model.DiscountSource;
import id.seapedia.seapediaprojectbe.model.DiscountValueType;

import java.util.UUID;

public record DiscountResolution(
        DiscountSource source,
        UUID discountId,
        String code,
        DiscountValueType discountType,
        Long discountValue,
        long discountAmount,
        String label
) {
    public static DiscountResolution none() {
        return new DiscountResolution(DiscountSource.NONE, null, null, null, null, 0L, null);
    }
}