package id.seapedia.seapediaprojectbe.service.impl;

import id.seapedia.seapediaprojectbe.dto.cart.*;
import id.seapedia.seapediaprojectbe.exception.BadRequestException;
import id.seapedia.seapediaprojectbe.exception.ResourceNotFoundException;
import id.seapedia.seapediaprojectbe.model.*;
import id.seapedia.seapediaprojectbe.repository.*;
import id.seapedia.seapediaprojectbe.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;

    private Cart getOrCreateCart(UUID buyerId) {
        return cartRepository.findByBuyerId(buyerId)
                .orElseGet(() -> cartRepository.save(
                        Cart.builder().buyerId(buyerId).build()
                ));
    }

    private CartSummaryResponse toSummary(Cart cart) {
        List<CartItem> items = cart.getItems();

        String storeName = null;
        if (cart.getStoreId() != null) {
            storeName = storeRepository.findById(cart.getStoreId())
                    .map(Store::getName)
                    .orElse(null);
        }

        List<CartItemResponse> itemResponses = items.stream()
                .map(ci -> CartItemResponse.builder()
                        .cartItemId(ci.getId())
                        .productId(ci.getProduct().getId())
                        .productName(ci.getProduct().getName())
                        .productPrice(ci.getProduct().getPrice())
                        .quantity(ci.getQuantity())
                        .subtotal(ci.getProduct().getPrice() * ci.getQuantity())
                        .build())
                .toList();

        long grandTotal = itemResponses.stream()
                .mapToLong(CartItemResponse::getSubtotal)
                .sum();

        return CartSummaryResponse.builder()
                .cartId(cart.getId())
                .storeId(cart.getStoreId())
                .storeName(storeName)
                .items(itemResponses)
                .totalItems(itemResponses.size())
                .grandTotal(grandTotal)
                .build();
    }

    @Override
    @Transactional
    public CartSummaryResponse addItem(UUID buyerId, AddToCartRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Produk tidak ditemukan"));

        if (product.getStock() < request.getQuantity()) {
            throw new BadRequestException("Stok produk tidak mencukupi");
        }

        Cart cart = getOrCreateCart(buyerId);

        UUID incomingStoreId = product.getStore().getId();
        if (cart.getStoreId() == null) {
            cart.setStoreId(incomingStoreId);
        } else if (!cart.getStoreId().equals(incomingStoreId)) {
            String existingStoreName = storeRepository.findById(cart.getStoreId())
                    .map(Store::getName).orElse("toko lain");
            String incomingStoreName = storeRepository.findById(incomingStoreId)
                    .map(Store::getName).orElse("toko ini");
            throw new BadRequestException(
                    "Keranjang kamu sudah berisi produk dari toko \"" + existingStoreName + "\". " +
                            "Produk \"" + product.getName() + "\" berasal dari toko \"" + incomingStoreName + "\". " +
                            "Selesaikan atau kosongkan keranjang sebelum berbelanja dari toko lain."
            );
        }

        var existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQty = item.getQuantity() + request.getQuantity();
            if (product.getStock() < newQty) {
                throw new BadRequestException("Stok tidak mencukupi untuk jumlah yang diminta");
            }
            item.setQuantity(newQty);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cart.getItems().add(newItem);
        }

        Cart saved = cartRepository.save(cart);
        log.info("[Cart] buyer={} addItem productId={} qty={}", buyerId, product.getId(), request.getQuantity());
        return toSummary(saved);
    }

    @Override
    @Transactional
    public CartSummaryResponse updateItem(UUID buyerId, UUID cartItemId, UpdateCartItemRequest request) {
        Cart cart = cartRepository.findByBuyerId(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart tidak ditemukan"));

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item cart tidak ditemukan"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Item bukan milik cart kamu");
        }

        if (item.getProduct().getStock() < request.getQuantity()) {
            throw new BadRequestException("Stok tidak mencukupi");
        }

        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        Cart refreshed = cartRepository.findByBuyerId(buyerId).orElseThrow();
        log.info("[Cart] buyer={} updateItem cartItemId={} qty={}", buyerId, cartItemId, request.getQuantity());
        return toSummary(refreshed);
    }

    @Override
    @Transactional
    public CartSummaryResponse removeItem(UUID buyerId, UUID cartItemId) {
        Cart cart = cartRepository.findByBuyerId(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart tidak ditemukan"));

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item cart tidak ditemukan"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Item bukan milik cart kamu");
        }

        cart.getItems().remove(item);
        cartItemRepository.delete(item);

        if (cart.getItems().isEmpty()) {
            cart.setStoreId(null);
        }

        Cart saved = cartRepository.save(cart);
        log.info("[Cart] buyer={} removeItem cartItemId={}", buyerId, cartItemId);
        return toSummary(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CartSummaryResponse getCart(UUID buyerId) {
        Cart cart = getOrCreateCart(buyerId);
        return toSummary(cart);
    }

    @Override
    @Transactional
    public void clearCart(UUID buyerId) {
        Cart cart = cartRepository.findByBuyerId(buyerId).orElse(null);
        if (cart != null) {
            cart.getItems().clear();
            cart.setStoreId(null);
            cartRepository.save(cart);
            log.info("[Cart] buyer={} cart cleared", buyerId);
        }
    }
}
