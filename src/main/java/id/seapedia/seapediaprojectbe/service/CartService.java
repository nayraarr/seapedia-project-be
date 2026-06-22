package id.seapedia.seapediaprojectbe.service;

import id.seapedia.seapediaprojectbe.dto.cart.AddToCartRequest;
import id.seapedia.seapediaprojectbe.dto.cart.CartSummaryResponse;
import id.seapedia.seapediaprojectbe.dto.cart.UpdateCartItemRequest;

import java.util.UUID;

public interface CartService {
    CartSummaryResponse addItem(UUID buyerId, AddToCartRequest request);
    CartSummaryResponse updateItem(UUID buyerId, UUID cartItemId, UpdateCartItemRequest request);
    CartSummaryResponse removeItem(UUID buyerId, UUID cartItemId);
    CartSummaryResponse getCart(UUID buyerId);
    void clearCart(UUID buyerId);
}