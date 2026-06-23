package id.seapedia.seapediaprojectbe.service;

import id.seapedia.seapediaprojectbe.dto.discount.PromoCreateRequest;
import id.seapedia.seapediaprojectbe.dto.discount.PromoResponse;

import java.util.List;
import java.util.UUID;

public interface PromoService {
    PromoResponse generatePromo(PromoCreateRequest request);
    List<PromoResponse> listPromos();
    PromoResponse getPromoDetail(UUID id);
}