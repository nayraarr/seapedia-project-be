package id.seapedia.seapediaprojectbe.service;

import id.seapedia.seapediaprojectbe.dto.store.StoreRequest;
import id.seapedia.seapediaprojectbe.dto.store.StoreResponse;

import java.util.List;
import java.util.UUID;

public interface StoreService {
    StoreResponse createStore(StoreRequest request, UUID ownerId);
    StoreResponse updateStore(StoreRequest request, UUID ownerId);
    StoreResponse getMyStore(UUID ownerId);
    List<StoreResponse> getAllStores();
    StoreResponse getStoreById(UUID storeId);
}
