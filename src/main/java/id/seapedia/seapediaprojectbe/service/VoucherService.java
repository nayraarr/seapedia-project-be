package id.seapedia.seapediaprojectbe.service;

import id.seapedia.seapediaprojectbe.dto.discount.VoucherCreateRequest;
import id.seapedia.seapediaprojectbe.dto.discount.VoucherResponse;

import java.util.List;
import java.util.UUID;

public interface VoucherService {
    VoucherResponse generateVoucher(VoucherCreateRequest request);
    List<VoucherResponse> listVouchers();
    List<VoucherResponse> listAllVouchers();
    VoucherResponse getVoucherDetail(UUID id);
}