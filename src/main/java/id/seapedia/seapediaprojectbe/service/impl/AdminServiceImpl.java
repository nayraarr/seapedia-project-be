package id.seapedia.seapediaprojectbe.service.impl;

import id.seapedia.seapediaprojectbe.dto.admin.AdminDashboardResponse;
import id.seapedia.seapediaprojectbe.dto.admin.AdminDeliveryJobResponse;
import id.seapedia.seapediaprojectbe.dto.admin.AdminOrderResponse;
import id.seapedia.seapediaprojectbe.dto.admin.AdminUserResponse;
import id.seapedia.seapediaprojectbe.model.*;
import id.seapedia.seapediaprojectbe.repository.*;
import id.seapedia.seapediaprojectbe.service.AdminService;
import id.seapedia.seapediaprojectbe.service.SimulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final VoucherRepository voucherRepository;
    private final PromoRepository promoRepository;
    private final DeliveryJobRepository deliveryJobRepository;
    private final SimulationService simulationService;

    @Override
    public AdminDashboardResponse getDashboardSummary() {

        long totalUsers  = userRepository.count();
        long totalAdmins = userRepository.findAll().stream()
                .filter(u -> Boolean.TRUE.equals(u.getIsAdmin())).count();
        long totalBuyers  = userRoleRepository.countByRole(RoleType.BUYER);
        long totalSellers = userRoleRepository.countByRole(RoleType.SELLER);
        long totalDrivers = userRoleRepository.countByRole(RoleType.DRIVER);

        long totalStores = storeRepository.count();

        List<Product> allProducts = productRepository.findAll();
        long totalProducts    = allProducts.size();
        long outOfStock       = allProducts.stream().filter(p -> p.getStock() == 0).count();
        long lowStock         = allProducts.stream().filter(p -> p.getStock() > 0 && p.getStock() <= 5).count();

        long totalOrders       = orderRepository.count();
        long oSedangDikemas    = orderRepository.countByStatus(OrderStatus.SEDANG_DIKEMAS);
        long oMenungguPengirim = orderRepository.countByStatus(OrderStatus.MENUNGGU_PENGIRIM);
        long oDikirim          = orderRepository.countByStatus(OrderStatus.SEDANG_DIKIRIM);
        long oSelesai          = orderRepository.countByStatus(OrderStatus.SELESAI);
        long oDikembalikan     = orderRepository.countByStatus(OrderStatus.DIKEMBALIKAN);
        Long totalRevenue      = orderRepository.sumTotalRevenue();

        List<Voucher> allVouchers = voucherRepository.findAll();
        long totalVouchers   = allVouchers.size();
        long activeVouchers  = allVouchers.stream()
                .filter(v -> Boolean.TRUE.equals(v.getActive()) && !v.isExpired() && v.hasRemainingUsage())
                .count();
        long expiredVouchers = allVouchers.stream().filter(Voucher::isExpired).count();

        List<Promo> allPromos = promoRepository.findAll();
        long totalPromos   = allPromos.size();
        long activePromos  = allPromos.stream()
                .filter(p -> Boolean.TRUE.equals(p.getActive()) && !p.isExpired())
                .count();
        long expiredPromos = allPromos.stream().filter(Promo::isExpired).count();

        long totalDeliveryJobs = deliveryJobRepository.count();
        long unassignedJobs    = deliveryJobRepository.countByDriverIdIsNull();
        long ongoingJobs       = deliveryJobRepository.countByDriverIdIsNotNullAndCompletedAtIsNull();
        long completedJobs     = deliveryJobRepository.countByCompletedAtIsNotNull();

        LocalDateTime simulatedNow = simulationService.now();
        List<OrderStatus> finalStatuses = List.of(
                OrderStatus.SELESAI, OrderStatus.DIKEMBALIKAN);

        List<Order> overdueList = new java.util.ArrayList<>();
        for (DeliveryMethod method : DeliveryMethod.values()) {
            LocalDateTime threshold = simulatedNow.minusMinutes(method.getSlaSinceCreatedMinutes());
            overdueList.addAll(orderRepository.findOverdueByDeliveryMethod(finalStatuses, threshold, method));
        }
        List<Order> recentProcessed = orderRepository.findByStatusAndUpdatedAtSince(
                OrderStatus.DIKEMBALIKAN, simulatedNow.minusHours(24));

        long overdueCount = overdueList.size() + recentProcessed.size();

        List<AdminDashboardResponse.OverdueOrderItem> overdueItems = overdueList.stream()
                .map(o -> {
                    long minutesOverdue = java.time.Duration.between(o.getUpdatedAt(), simulatedNow).toMinutes();
                    return AdminDashboardResponse.OverdueOrderItem.builder()
                            .orderId(o.getId().toString())
                            .storeName(o.getStoreName())
                            .buyerUsername(o.getBuyerUsername())
                            .status(o.getStatus().getLabel())
                            .createdAt(o.getCreatedAt().toString())
                            .minutesOverdue(minutesOverdue)
                            .processed(false)
                            .build();
                })
                .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));

        for (Order o : recentProcessed) {
            if (overdueList.stream().anyMatch(ex -> ex.getId().equals(o.getId()))) continue;
            long minutesSinceUpdate = java.time.Duration.between(o.getUpdatedAt(), simulatedNow).toMinutes();
            overdueItems.add(AdminDashboardResponse.OverdueOrderItem.builder()
                    .orderId(o.getId().toString())
                    .storeName(o.getStoreName())
                    .buyerUsername(o.getBuyerUsername())
                    .status(o.getStatus().getLabel())
                    .createdAt(o.getCreatedAt().toString())
                    .minutesOverdue(minutesSinceUpdate)
                    .processed(true)
                    .build());
        }

        return AdminDashboardResponse.builder()
                .totalUsers(totalUsers)
                .totalAdmins(totalAdmins)
                .totalBuyers(totalBuyers)
                .totalSellers(totalSellers)
                .totalDrivers(totalDrivers)
                .totalStores(totalStores)
                .totalProducts(totalProducts)
                .outOfStockProducts(outOfStock)
                .lowStockProducts(lowStock)
                .totalOrders(totalOrders)
                .ordersSedangDikemas(oSedangDikemas)
                .ordersMenungguPengirim(oMenungguPengirim)
                .ordersDikirim(oDikirim)
                .ordersSelesai(oSelesai)
                .ordersDikembalikan(oDikembalikan)
                .totalRevenue(totalRevenue)
                .totalVouchers(totalVouchers)
                .activeVouchers(activeVouchers)
                .expiredVouchers(expiredVouchers)
                .totalPromos(totalPromos)
                .activePromos(activePromos)
                .expiredPromos(expiredPromos)
                .totalDeliveryJobs(totalDeliveryJobs)
                .unassignedJobs(unassignedJobs)
                .ongoingJobs(ongoingJobs)
                .completedJobs(completedJobs)
                .overdueOrders(overdueCount)
                .overdueOrderList(overdueItems)
                .simulationOffsetMinutes(simulationService.getOffsetMinutes())
                .build();
    }

    @Override
    public List<AdminUserResponse> listUsers() {
        return userRepository.findAll().stream().map(u -> AdminUserResponse.builder()
                .id(u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .isAdmin(Boolean.TRUE.equals(u.getIsAdmin()))
                .roles(u.getRoles().stream().map(r -> r.getRole().name()).toList())
                .createdAt(u.getCreatedAt())
                .build()).toList();
    }

    @Override
    public List<AdminOrderResponse> listOrders() {
        return orderRepository.findAll().stream().map(o -> AdminOrderResponse.builder()
                .id(o.getId())
                .storeName(o.getStoreName())
                .buyerUsername(o.getBuyerUsername())
                .status(o.getStatus().name())
                .statusLabel(o.getStatus().getLabel())
                .totalAmount(o.getTotalAmount())
                .deliveryMethod(o.getDeliveryMethod().name())
                .createdAt(o.getCreatedAt())
                .build()).toList();
    }

    @Override
    public List<AdminDeliveryJobResponse> listDeliveryJobs() {
        return deliveryJobRepository.findAll().stream().map(j -> AdminDeliveryJobResponse.builder()
                .id(j.getId())
                .orderId(j.getOrder().getId())
                .storeName(j.getOrder().getStoreName())
                .driverId(j.getDriverId())
                .orderStatus(j.getOrder().getStatus().name())
                .orderStatusLabel(j.getOrder().getStatus().getLabel())
                .createdAt(j.getCreatedAt())
                .build()).toList();
    }
}