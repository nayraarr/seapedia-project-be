package id.seapedia.seapediaprojectbe.service.impl;

import id.seapedia.seapediaprojectbe.dto.address.AddressRequest;
import id.seapedia.seapediaprojectbe.dto.address.AddressResponse;
import id.seapedia.seapediaprojectbe.exception.ResourceNotFoundException;
import id.seapedia.seapediaprojectbe.model.Address;
import id.seapedia.seapediaprojectbe.repository.AddressRepository;
import id.seapedia.seapediaprojectbe.service.AddressService;
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
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    private AddressResponse toResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .label(address.getLabel())
                .recipientName(address.getRecipientName())
                .phone(address.getPhone())
                .fullAddress(address.getFullAddress())
                .city(address.getCity())
                .postalCode(address.getPostalCode())
                .isDefault(address.getIsDefault())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getMyAddresses(UUID userId) {
        log.info("[getMyAddresses]  userId={}", userId);
        return addressRepository.findByUserId(userId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public AddressResponse createAddress(UUID userId, AddressRequest request) {
        log.info("[createAddress]  userId={} label={}", userId, request.getLabel());

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            unsetAllDefaults(userId);
        }

        Address address = Address.builder()
                .userId(userId)
                .label(SanitizerUtil.clean(request.getLabel()))
                .recipientName(SanitizerUtil.clean(request.getRecipientName()))
                .phone(request.getPhone())
                .fullAddress(SanitizerUtil.clean(request.getFullAddress()))
                .city(SanitizerUtil.clean(request.getCity()))
                .postalCode(request.getPostalCode())
                .isDefault(request.getIsDefault())
                .build();

        address = addressRepository.save(address);
        log.info("[createAddress]  addressId={}", address.getId());
        return toResponse(address);
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(UUID userId, UUID addressId, AddressRequest request) {
        log.info("[updateAddress]  userId={} addressId={}", userId, addressId);

        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> {
                    log.warn("[updateAddress]  not found: addressId={}", addressId);
                    return new ResourceNotFoundException("Address not found");
                });

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            unsetAllDefaults(userId);
        }

        address.setLabel(SanitizerUtil.clean(request.getLabel()));
        address.setRecipientName(SanitizerUtil.clean(request.getRecipientName()));
        address.setPhone(request.getPhone());
        address.setFullAddress(SanitizerUtil.clean(request.getFullAddress()));
        address.setCity(SanitizerUtil.clean(request.getCity()));
        address.setPostalCode(request.getPostalCode());
        address.setIsDefault(request.getIsDefault());

        address = addressRepository.save(address);
        log.info("[updateAddress]  addressId={}", address.getId());
        return toResponse(address);
    }

    @Override
    @Transactional
    public void deleteAddress(UUID userId, UUID addressId) {
        log.info("[deleteAddress]  userId={} addressId={}", userId, addressId);
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> {
                    log.warn("[deleteAddress]  not found: addressId={}", addressId);
                    return new ResourceNotFoundException("Address not found");
                });
        addressRepository.delete(address);
        log.info("[deleteAddress]  deleted addressId={}", addressId);
    }

    @Override
    @Transactional
    public AddressResponse setDefaultAddress(UUID userId, UUID addressId) {
        log.info("[setDefaultAddress]  userId={} addressId={}", userId, addressId);
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> {
                    log.warn("[setDefaultAddress]  not found: addressId={}", addressId);
                    return new ResourceNotFoundException("Address not found");
                });

        unsetAllDefaults(userId);
        address.setIsDefault(true);
        address = addressRepository.save(address);
        log.info("[setDefaultAddress]  default set to addressId={}", addressId);
        return toResponse(address);
    }

    private void unsetAllDefaults(UUID userId) {
        List<Address> defaults = addressRepository.findByUserIdAndIsDefaultTrue(userId);
        defaults.forEach(a -> a.setIsDefault(false));
        addressRepository.saveAll(defaults);
    }
}