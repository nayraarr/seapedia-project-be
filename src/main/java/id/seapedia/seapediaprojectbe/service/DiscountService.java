package id.seapedia.seapediaprojectbe.service;

import id.seapedia.seapediaprojectbe.dto.discount.DiscountValidationResponse;

public interface DiscountService {

    DiscountResolution resolve(String code, long subtotal);

    DiscountValidationResponse validate(String code, long subtotal);

    void consume(DiscountResolution resolution);
}