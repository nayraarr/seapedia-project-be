package id.seapedia.seapediaprojectbe.service.impl;

import id.seapedia.seapediaprojectbe.dto.admin.OverdueProcessResult;
import id.seapedia.seapediaprojectbe.exception.ResourceNotFoundException;
import id.seapedia.seapediaprojectbe.model.*;
import id.seapedia.seapediaprojectbe.repository.*;
import id.seapedia.seapediaprojectbe.service.OverdueService;
import id.seapedia.seapediaprojectbe.service.SimulationService;
import id.seapedia.seapediaprojectbe.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OverdueServiceImpl implements OverdueService {

    private static final List<OrderStatus> FINAL_STATUSES = List.of(
            OrderStatus.SELESAI, OrderStatus.DIKEMBALIKAN
    );

    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final WalletService walletService;
    private final SimulationService simulationService;

    @Override
    @Transactional
    public List<OverdueProcessResult> processAllOverdueOrders() {
        LocalDateTime now = simulationService.now();
        List<OverdueProcessResult> results = new ArrayList<>();

        for (DeliveryMethod method : DeliveryMethod.values()) {
            LocalDateTime threshold = now.minusMinutes(method.getSlaSinceCreatedMinutes());
            List<Order> overdueOrders = orderRepository.findOverdueByDeliveryMethod(
                    FINAL_STATUSES, threshold, method);

            log.info("[processAllOverdueOrders] method={} threshold={} found={} overdue orders",
                    method, threshold, overdueOrders.size());

            for (Order order : overdueOrders) {
                try {
                    OverdueProcessResult result = doProcess(order, now);
                    results.add(result);
                } catch (Exception e) {
                    log.error("[processAllOverdueOrders] Failed to process orderId={}: {}",
                            order.getId(), e.getMessage());
                    results.add(OverdueProcessResult.builder()
                            .orderId(order.getId())
                            .storeName(order.getStoreName())
                            .buyerUsername(order.getBuyerUsername())
                            .skipped(true)
                            .note("Error saat proses: " + e.getMessage())
                            .build());
                }
            }
        }

        return results;
    }

    @Scheduled(fixedRate = 1800000)
    @Transactional
    public void scheduledProcessOverdueOrders() {
        log.info("[scheduledProcessOverdueOrders] Menjalankan pemrosesan overdue otomatis setiap 30 menit...");
        List<OverdueProcessResult> results = processAllOverdueOrders();
        log.info("[scheduledProcessOverdueOrders] Selesai: {} order diproses", results.size());
    }

    @Override
    @Transactional
    public OverdueProcessResult processOverdueOrder(UUID orderId) {
        Order order = orderRepository.findActiveOrderById(orderId, FINAL_STATUSES)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order tidak ditemukan atau sudah dalam status final"));
        return doProcess(order, simulationService.now());
    }

    private OverdueProcessResult doProcess(Order order, LocalDateTime now) {
        if (FINAL_STATUSES.contains(order.getStatus())) {
            log.warn("[doProcess] orderId={} already in final status={}, skipping",
                    order.getId(), order.getStatus());
            return OverdueProcessResult.builder()
                    .orderId(order.getId())
                    .storeName(order.getStoreName())
                    .buyerUsername(order.getBuyerUsername())
                    .previousStatus(order.getStatus().name())
                    .skipped(true)
                    .note("Sudah dalam status final, dilewati")
                    .build();
        }

        String previousStatus = order.getStatus().name();

        List<OrderItem> items = orderItemRepository.findByOrderIdOrderByCreatedAtAsc(order.getId());
        for (OrderItem item : items) {
            productRepository.findById(item.getProductId()).ifPresent(product -> {
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
                log.info("[doProcess] Restored stock productId={} qty={}", item.getProductId(), item.getQuantity());
            });
        }

        Long refundedAmount = null;
        walletService.refundBalance(
                order.getBuyerId(),
                order.getTotalAmount(),
                "Refund otomatis order #" + order.getId().toString().substring(0, 8) +
                        " (overdue - " + order.getDeliveryMethod().getLabel() + ")"
        );
        refundedAmount = order.getTotalAmount();
        log.info("[doProcess] Refunded orderId={} amount={}", order.getId(), refundedAmount);

        order.setStatus(OrderStatus.DIKEMBALIKAN);
        orderRepository.save(order);

        String note = String.format(
                "Order dikembalikan otomatis karena melewati SLA %s (%d menit). Refund Rp%,d ke wallet buyer.",
                order.getDeliveryMethod().getLabel(),
                order.getDeliveryMethod().getSlaSinceCreatedMinutes(),
                order.getTotalAmount()
        );
        orderStatusHistoryRepository.save(OrderStatusHistory.builder()
                .order(order)
                .status(OrderStatus.DIKEMBALIKAN)
                .note(note)
                .build());

        log.info("[doProcess] orderId={} processed: {} -> DIKEMBALIKAN, refund={}, stockRestored=true",
                order.getId(), previousStatus, refundedAmount);

        return OverdueProcessResult.builder()
                .orderId(order.getId())
                .storeName(order.getStoreName())
                .buyerUsername(order.getBuyerUsername())
                .deliveryMethod(order.getDeliveryMethod().getLabel())
                .previousStatus(previousStatus)
                .newStatus(OrderStatus.DIKEMBALIKAN.name())
                .refundedAmount(refundedAmount)
                .stockRestored(true)
                .note(note)
                .skipped(false)
                .build();
    }
}