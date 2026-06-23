package id.seapedia.seapediaprojectbe.service.impl;

import id.seapedia.seapediaprojectbe.dto.discount.DiscountValidationResponse;
import id.seapedia.seapediaprojectbe.dto.order.*;
import id.seapedia.seapediaprojectbe.exception.BadRequestException;
import id.seapedia.seapediaprojectbe.exception.ResourceNotFoundException;
import id.seapedia.seapediaprojectbe.model.*;
import id.seapedia.seapediaprojectbe.repository.*;
import id.seapedia.seapediaprojectbe.service.OrderService;
import id.seapedia.seapediaprojectbe.service.WalletService;
import id.seapedia.seapediaprojectbe.service.DiscountResolution;
import id.seapedia.seapediaprojectbe.service.DiscountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final int TAX_RATE_PERCENT = 12;

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final AddressRepository addressRepository;
    private final WalletRepository walletRepository;
    private final WalletService walletService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final UserRepository userRepository;
    private final DiscountService discountService;

    private record OrderComputation(
            Cart cart,
            Store store,
            Address address,
            List<CartItem> items,
            long subtotal,
            DiscountResolution discount,
            long deliveryFee,
            long taxBase,
            long taxAmount,
            long totalAmount,
            long walletBalance,
            boolean walletSufficient
    ) {}

    private Address resolveAddress(UUID buyerId, UUID addressId) {
        if (addressId != null) {
            return addressRepository.findByIdAndUserId(addressId, buyerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        }

        return addressRepository.findFirstByUserIdAndIsDefaultTrue(buyerId)
                .orElseThrow(() -> new BadRequestException("Default address not found"));
    }

    private OrderComputation computeCheckout(UUID buyerId, CheckoutRequest request, boolean forUpdate) {
        Cart cart = cartRepository.findByBuyerId(buyerId)
                .orElseThrow(() -> new BadRequestException("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        if (cart.getStoreId() == null) {
            throw new BadRequestException("Cart does not have a store");
        }

        Store store = storeRepository.findById(cart.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

        Address address = resolveAddress(buyerId, request.getAddressId());

        List<CartItem> items = new ArrayList<>(cart.getItems());
        long subtotal = 0L;

        for (CartItem cartItem : items) {
            Product product = forUpdate
                    ? productRepository.findByIdForUpdate(cartItem.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"))
                    : productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            if (!product.getStore().getId().equals(store.getId())) {
                throw new BadRequestException("Cart contains product from another store");
            }

            if (forUpdate && product.getStock() < cartItem.getQuantity()) {
                throw new BadRequestException("Stok produk tidak mencukupi");
            }

            subtotal += product.getPrice() * cartItem.getQuantity();
        }

        DiscountResolution discount = discountService.resolve(request.getDiscountCode(), subtotal);

        long discountAmount = discount.discountAmount();
        long deliveryFee = request.getDeliveryMethod().getFee();
        long taxBase = subtotal - discountAmount;
        long taxAmount = Math.round(taxBase * TAX_RATE_PERCENT / 100.0);
        long totalAmount = taxBase + deliveryFee + taxAmount;

        long walletBalance = walletRepository.findByUserId(buyerId)
                .map(Wallet::getBalance)
                .orElse(0L);

        return new OrderComputation(
                cart,
                store,
                address,
                items,
                subtotal,
                discount,
                deliveryFee,
                taxBase,
                taxAmount,
                totalAmount,
                walletBalance,
                walletBalance >= totalAmount
        );
    }

    private CheckoutSummaryResponse toCheckoutSummary(OrderComputation computation, DeliveryMethod method) {
        List<CheckoutItemResponse> itemResponses = computation.items().stream()
                .map(item -> CheckoutItemResponse.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .unitPrice(item.getProduct().getPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getProduct().getPrice() * item.getQuantity())
                        .build())
                .toList();

        Address address = computation.address();
        AddressSnapshotResponse addressSnapshot = AddressSnapshotResponse.builder()
                .addressId(address.getId())
                .recipientName(address.getRecipientName())
                .phone(address.getPhone())
                .fullAddress(address.getFullAddress())
                .city(address.getCity())
                .postalCode(address.getPostalCode())
                .build();

        return CheckoutSummaryResponse.builder()
                .cartId(computation.cart().getId())
                .storeId(computation.store().getId())
                .storeName(computation.store().getName())
                .address(addressSnapshot)
                .deliveryMethod(method)
                .deliveryMethodLabel(method.getLabel())
                .subtotal(computation.subtotal())
                .discountCode(computation.discount().code())
                .discountSource(computation.discount().source())
                .discountLabel(computation.discount().label())
                .discountAmount(computation.discount().discountAmount())
                .deliveryFee(computation.deliveryFee())
                .taxRatePercent(TAX_RATE_PERCENT)
                .taxBase(computation.taxBase())
                .taxAmount(computation.taxAmount())
                .totalAmount(computation.totalAmount())
                .walletBalance(computation.walletBalance())
                .walletSufficient(computation.walletSufficient())
                .items(itemResponses)
                .build();
    }

    private OrderSummaryResponse toSummary(Order order) {
        return OrderSummaryResponse.builder()
                .orderId(order.getId())
                .storeId(order.getStoreId())
                .storeName(order.getStoreName())
                .buyerId(order.getBuyerId())
                .buyerUsername(order.getBuyerUsername())
                .deliveryMethod(order.getDeliveryMethod())
                .deliveryMethodLabel(order.getDeliveryMethod().getLabel())
                .status(order.getStatus())
                .statusLabel(order.getStatus().getLabel())
                .subtotal(order.getSubtotal())
                .discountCode(order.getDiscountCode())
                .discountSource(order.getDiscountSource())
                .discountAmount(order.getDiscountAmount())
                .deliveryFee(order.getDeliveryFee())
                .taxRatePercent(order.getTaxRatePercent())
                .taxAmount(order.getTaxAmount())
                .totalAmount(order.getTotalAmount())
                .itemCount(order.getItems().size())
                .createdAt(order.getCreatedAt())
                .build();
    }

    private OrderDetailResponse toDetail(Order order) {
        List<OrderItemResponse> items = orderItemRepository.findByOrderIdOrderByCreatedAtAsc(order.getId())
                .stream()
                .map(item -> OrderItemResponse.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .unitPrice(item.getUnitPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getSubtotal())
                        .build())
                .toList();

        List<OrderStatusHistoryResponse> history = orderStatusHistoryRepository.findByOrderIdOrderByCreatedAtAsc(order.getId())
                .stream()
                .map(status -> OrderStatusHistoryResponse.builder()
                        .status(status.getStatus().name())
                        .statusLabel(status.getStatus().getLabel())
                        .changedAt(status.getCreatedAt())
                        .note(status.getNote())
                        .build())
                .toList();

        AddressSnapshotResponse shippingAddress = AddressSnapshotResponse.builder()
                .addressId(order.getAddressId())
                .recipientName(order.getRecipientName())
                .phone(order.getPhone())
                .fullAddress(order.getFullAddress())
                .city(order.getCity())
                .postalCode(order.getPostalCode())
                .build();

        return OrderDetailResponse.builder()
                .orderId(order.getId())
                .buyerId(order.getBuyerId())
                .buyerUsername(order.getBuyerUsername())
                .storeId(order.getStoreId())
                .storeName(order.getStoreName())
                .shippingAddress(shippingAddress)
                .deliveryMethod(order.getDeliveryMethod())
                .deliveryMethodLabel(order.getDeliveryMethod().getLabel())
                .status(order.getStatus())
                .statusLabel(order.getStatus().getLabel())
                .subtotal(order.getSubtotal())
                .discountCode(order.getDiscountCode())
                .discountSource(order.getDiscountSource())
                .discountAmount(order.getDiscountAmount())
                .deliveryFee(order.getDeliveryFee())
                .taxRatePercent(order.getTaxRatePercent())
                .taxBase(order.getTaxBase())
                .taxAmount(order.getTaxAmount())
                .totalAmount(order.getTotalAmount())
                .walletBalanceBefore(order.getWalletBalanceBefore())
                .walletBalanceAfter(order.getWalletBalanceAfter())
                .items(items)
                .statusHistory(history)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private Order getOrderForBuyer(UUID buyerId, UUID orderId) {
        return orderRepository.findByIdAndBuyerId(orderId, buyerId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    private Order getOrderForSeller(UUID sellerId, UUID orderId) {
        Store store = storeRepository.findByOwnerId(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

        return orderRepository.findByIdAndStoreId(orderId, store.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public CheckoutSummaryResponse previewCheckout(UUID buyerId, CheckoutRequest request) {
        log.info("[previewCheckout] buyerId={} deliveryMethod={}", buyerId, request.getDeliveryMethod());
        OrderComputation computation = computeCheckout(buyerId, request, false);
        return toCheckoutSummary(computation, request.getDeliveryMethod());
    }

    @Override
    @Transactional
    public OrderDetailResponse createOrder(UUID buyerId, CheckoutRequest request) {
        log.info("[createOrder] buyerId={} deliveryMethod={}", buyerId, request.getDeliveryMethod());
        OrderComputation computation = computeCheckout(buyerId, request, true);

        if (!computation.walletSufficient()) {
            throw new BadRequestException("Saldo wallet tidak mencukupi");
        }

        discountService.consume(computation.discount());

        Wallet wallet = walletRepository.findByUserIdForUpdate(buyerId)
                .orElseGet(() -> walletRepository.save(Wallet.builder().userId(buyerId).balance(0L).build()));
        long balanceBefore = wallet.getBalance();

        for (CartItem cartItem : computation.items()) {
            Product product = productRepository.findByIdForUpdate(cartItem.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }

        walletService.deductBalance(
                buyerId,
                computation.totalAmount(),
                "Checkout order dari toko " + computation.store().getName()
        );

        wallet = walletRepository.findByUserIdForUpdate(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        String buyerUsername = userRepository.findById(buyerId)
                .map(user -> user.getUsername())
                .orElse("Unknown");

        Order orderToSave = Order.builder()
                .buyerId(buyerId)
                .buyerUsername(buyerUsername)
                .storeId(computation.store().getId())
                .storeName(computation.store().getName())
                .addressId(computation.address().getId())
                .recipientName(computation.address().getRecipientName())
                .phone(computation.address().getPhone())
                .fullAddress(computation.address().getFullAddress())
                .city(computation.address().getCity())
                .postalCode(computation.address().getPostalCode())
                .deliveryMethod(request.getDeliveryMethod())
                .status(OrderStatus.SEDANG_DIKEMAS)
                .subtotal(computation.subtotal())
                .discountCode(computation.discount().code())
                .discountSource(computation.discount().source())
                .discountAmount(computation.discount().discountAmount())
                .deliveryFee(computation.deliveryFee())
                .taxRatePercent(TAX_RATE_PERCENT)
                .taxBase(computation.taxBase())
                .taxAmount(computation.taxAmount())
                .totalAmount(computation.totalAmount())
                .walletBalanceBefore(balanceBefore)
                .walletBalanceAfter(wallet.getBalance())
                .build();

        List<OrderItem> orderItems = computation.items().stream()
                .map(cartItem -> OrderItem.builder()
                        .order(orderToSave)
                        .productId(cartItem.getProduct().getId())
                        .productName(cartItem.getProduct().getName())
                        .unitPrice(cartItem.getProduct().getPrice())
                        .quantity(cartItem.getQuantity())
                        .subtotal(cartItem.getProduct().getPrice() * cartItem.getQuantity())
                        .build())
                .toList();
        orderToSave.getItems().addAll(orderItems);

        orderToSave.getStatusHistories().add(OrderStatusHistory.builder()
                .order(orderToSave)
                .status(OrderStatus.SEDANG_DIKEMAS)
                .note("Order berhasil dibuat")
                .build());

        Order order = orderRepository.save(orderToSave);

        computation.cart().getItems().clear();
        computation.cart().setStoreId(null);
        cartRepository.save(computation.cart());

        log.info("[createOrder] created orderId={} totalAmount={}", order.getId(), order.getTotalAmount());
        return toDetail(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> getBuyerOrderHistory(UUID buyerId) {
        log.info("[getBuyerOrderHistory] buyerId={}", buyerId);
        return orderRepository.findByBuyerIdOrderByCreatedAtDesc(buyerId)
                .stream()
                .map(this::toSummary)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailResponse getBuyerOrderDetail(UUID buyerId, UUID orderId) {
        log.info("[getBuyerOrderDetail] buyerId={} orderId={}", buyerId, orderId);
        return toDetail(getOrderForBuyer(buyerId, orderId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> getSellerIncomingOrders(UUID sellerId) {
        log.info("[getSellerIncomingOrders] sellerId={}", sellerId);
        Store store = storeRepository.findByOwnerId(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

        return orderRepository.findByStoreIdOrderByCreatedAtDesc(store.getId())
                .stream()
                .map(this::toSummary)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailResponse getSellerOrderDetail(UUID sellerId, UUID orderId) {
        log.info("[getSellerOrderDetail] sellerId={} orderId={}", sellerId, orderId);
        return toDetail(getOrderForSeller(sellerId, orderId));
    }

    @Override
    @Transactional
    public OrderDetailResponse processOrder(UUID sellerId, UUID orderId) {
        log.info("[processOrder] sellerId={} orderId={}", sellerId, orderId);

        Order order = getOrderForSeller(sellerId, orderId);

        if (order.getStatus() != OrderStatus.SEDANG_DIKEMAS) {
            throw new BadRequestException(
                    "Order tidak bisa diproses dari status " + order.getStatus().getLabel());
        }

        order.setStatus(OrderStatus.MENUNGGU_PENGIRIM);
        Order savedOrder = orderRepository.save(order);

        orderStatusHistoryRepository.save(OrderStatusHistory.builder()
                .order(savedOrder)
                .status(OrderStatus.MENUNGGU_PENGIRIM)
                .note("Pesanan diproses oleh seller, menunggu pengirim mengambil pesanan")
                .build());

        log.info("[processOrder] orderId={} status -> MENUNGGU_PENGIRIM", orderId);
        return toDetail(savedOrder);
    }

    private List<StatusCountResponse> buildStatusBreakdown(List<Order> orders) {
        Map<OrderStatus, Long> counts = orders.stream()
                .collect(Collectors.groupingBy(Order::getStatus, LinkedHashMap::new, Collectors.counting()));

        return Arrays.stream(OrderStatus.values())
                .filter(counts::containsKey)
                .map(status -> StatusCountResponse.builder()
                        .status(status.name())
                        .statusLabel(status.getLabel())
                        .count(counts.get(status).intValue())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BuyerSpendingReportResponse getBuyerSpendingReport(UUID buyerId) {
        log.info("[getBuyerSpendingReport] buyerId={}", buyerId);
        List<Order> orders = orderRepository.findByBuyerIdOrderByCreatedAtDesc(buyerId);

        List<Order> validOrders = orders.stream()
                .filter(o -> o.getStatus() != OrderStatus.DIBATALKAN)
                .toList();

        long totalSpent = validOrders.stream().mapToLong(Order::getTotalAmount).sum();
        long totalDiscountSaved = validOrders.stream().mapToLong(Order::getDiscountAmount).sum();
        long totalDeliveryFee = validOrders.stream().mapToLong(Order::getDeliveryFee).sum();
        long totalTax = validOrders.stream().mapToLong(Order::getTaxAmount).sum();
        int cancelledOrders = (int) (orders.size() - validOrders.size());

        List<OrderSummaryResponse> recentOrders = orders.stream()
                .limit(5)
                .map(this::toSummary)
                .toList();

        return BuyerSpendingReportResponse.builder()
                .totalOrders(orders.size())
                .cancelledOrders(cancelledOrders)
                .totalSpent(totalSpent)
                .totalDiscountSaved(totalDiscountSaved)
                .totalDeliveryFee(totalDeliveryFee)
                .totalTax(totalTax)
                .statusBreakdown(buildStatusBreakdown(orders))
                .recentOrders(recentOrders)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public DiscountValidationResponse validateDiscountCode(UUID buyerId, String code) {
        log.info("[validateDiscountCode] buyerId={} code={}", buyerId, code);
        Cart cart = cartRepository.findByBuyerId(buyerId)
                .orElseThrow(() -> new BadRequestException("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        long subtotal = cart.getItems().stream()
                .mapToLong(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        return discountService.validate(code, subtotal);
    }
}