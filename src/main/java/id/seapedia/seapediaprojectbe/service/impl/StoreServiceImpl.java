package id.seapedia.seapediaprojectbe.service.impl;

import id.seapedia.seapediaprojectbe.dto.store.StoreRequest;
import id.seapedia.seapediaprojectbe.dto.store.StoreResponse;
import id.seapedia.seapediaprojectbe.exception.BadRequestException;
import id.seapedia.seapediaprojectbe.exception.ResourceNotFoundException;
import id.seapedia.seapediaprojectbe.model.Store;
import id.seapedia.seapediaprojectbe.repository.StoreRepository;
import id.seapedia.seapediaprojectbe.service.StoreService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;

    @Override
    @Transactional
    public StoreResponse createStore(StoreRequest request, UUID ownerId) {
        log.info("[createStore] 🚀 entry: ownerId={} name={}", ownerId, request.getName());

        if (storeRepository.existsByName(request.getName())) {
            log.warn("[createStore] ⚠️ store name already taken: name={}", request.getName());
            throw new BadRequestException("Store name already taken");
        }

        if (storeRepository.findByOwnerId(ownerId).isPresent()) {
            log.warn("[createStore] ⚠️ seller already has a store: ownerId={}", ownerId);
            throw new BadRequestException("You already have a store");
        }

        Store store = Store.builder()
                .name(request.getName())
                .description(request.getDescription())
                .ownerId(ownerId)
                .build();

        store = storeRepository.save(store);
        log.info("[createStore] ✅ store created: storeId={} name={}", store.getId(), store.getName());
        return toResponse(store);
    }

    @Override
    @Transactional
    public StoreResponse updateStore(StoreRequest request, UUID ownerId) {
        log.info("[updateStore] 🚀 entry: ownerId={} name={}", ownerId, request.getName());

        Store store = storeRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> {
                    log.warn("[updateStore] ⚠️ store not found: ownerId={}", ownerId);
                    return new ResourceNotFoundException("Store not found");
                });

        // cek uniqueness hanya kalau nama berubah
        if (!store.getName().equals(request.getName()) &&
                storeRepository.existsByName(request.getName())) {
            log.warn("[updateStore] ⚠️ store name already taken: name={}", request.getName());
            throw new BadRequestException("Store name already taken");
        }

        store.setName(request.getName());
        store.setDescription(request.getDescription());
        store = storeRepository.save(store);

        log.info("[updateStore] ✅ store updated: storeId={} name={}", store.getId(), store.getName());
        return toResponse(store);
    }

    @Override
    @Transactional(readOnly = true)
    public StoreResponse getMyStore(UUID ownerId) {
        log.info("[getMyStore] 🚀 entry: ownerId={}", ownerId);
        Store store = storeRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> {
                    log.warn("[getMyStore] ⚠️ store not found: ownerId={}", ownerId);
                    return new ResourceNotFoundException("Store not found");
                });
        log.info("[getMyStore] ✅ found: storeId={}", store.getId());
        return toResponse(store);
    }

    @Override
    public List<StoreResponse> getAllStores() {
        log.info("[getAllStores] 🚀 entry");
        List<Store> stores = storeRepository.findAll();
        log.info("[getAllStores] ✅ found {} stores", stores.size());
        return stores.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StoreResponse getStoreById(UUID storeId) {
        log.info("[getStoreById] 🚀 entry: storeId={}", storeId);
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> {
                    log.warn("[getStoreById] ⚠️ store not found: storeId={}", storeId);
                    return new ResourceNotFoundException("Store not found");
                });
        log.info("[getStoreById] ✅ found: storeId={}", store.getId());
        return toResponse(store);
    }

    private StoreResponse toResponse(Store store) {
        return StoreResponse.builder()
                .id(store.getId())
                .name(store.getName())
                .description(store.getDescription())
                .ownerId(store.getOwnerId())
                .createdAt(store.getCreatedAt())
                .updatedAt(store.getUpdatedAt())
                .build();
    }
}
