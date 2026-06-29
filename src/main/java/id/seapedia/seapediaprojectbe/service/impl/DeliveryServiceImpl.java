package id.seapedia.seapediaprojectbe.service.impl;

import id.seapedia.seapediaprojectbe.dto.delivery.DeliveryJobDetailResponse;
import id.seapedia.seapediaprojectbe.dto.delivery.DeliveryJobSummaryResponse;
import id.seapedia.seapediaprojectbe.dto.delivery.DriverIncomeReportResponse;
import id.seapedia.seapediaprojectbe.dto.order.AddressSnapshotResponse;
import id.seapedia.seapediaprojectbe.dto.order.OrderItemResponse;
import id.seapedia.seapediaprojectbe.dto.order.OrderStatusHistoryResponse;
import id.seapedia.seapediaprojectbe.exception.BadRequestException;
import id.seapedia.seapediaprojectbe.exception.ResourceNotFoundException;
import id.seapedia.seapediaprojectbe.model.DeliveryJob;
import id.seapedia.seapediaprojectbe.model.Order;
import id.seapedia.seapediaprojectbe.model.OrderStatus;
import id.seapedia.seapediaprojectbe.model.OrderStatusHistory;
import id.seapedia.seapediaprojectbe.model.Store;
import id.seapedia.seapediaprojectbe.repository.DeliveryJobRepository;
import id.seapedia.seapediaprojectbe.repository.OrderItemRepository;
import id.seapedia.seapediaprojectbe.repository.OrderRepository;
import id.seapedia.seapediaprojectbe.repository.OrderStatusHistoryRepository;
import id.seapedia.seapediaprojectbe.repository.StoreRepository;
import id.seapedia.seapediaprojectbe.service.DeliveryService;
import id.seapedia.seapediaprojectbe.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryJobRepository deliveryJobRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final StoreRepository storeRepository;
    private final WalletService walletService;

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryJobSummaryResponse> getAvailableJobs() {
        List<DeliveryJob> jobs = deliveryJobRepository
                .findByOrder_StatusOrderByCreatedAtAsc(OrderStatus.MENUNGGU_PENGIRIM);

        log.info("[getAvailableJobs] found {} available job(s)", jobs.size());

        return jobs.stream().map(this::toSummary).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryJobDetailResponse getJobDetail(UUID jobId, UUID driverId) {
        log.info("[getJobDetail] jobId={} driverId={}", jobId, driverId);

        DeliveryJob job = deliveryJobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery job not found"));

        if (job.getDriverId() != null && !job.getDriverId().equals(driverId)) {
            throw new BadRequestException("Anda tidak memiliki akses ke job ini");
        }

        return toDetail(job);
    }

    @Override
    @Transactional
    public DeliveryJobDetailResponse takeJob(UUID jobId, UUID driverId) {
        log.info("[takeJob] jobId={} driverId={}", jobId, driverId);

        DeliveryJob job = deliveryJobRepository
                .findByIdAndOrder_StatusForUpdate(jobId, OrderStatus.MENUNGGU_PENGIRIM)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery job not found or no longer available"));

        if (job.getDriverId() != null) {
            throw new BadRequestException("Job sudah diambil driver lain");
        }

        Order order = job.getOrder();
        if (!order.getStatus().canTransitionTo(OrderStatus.SEDANG_DIKIRIM)) {
            throw new BadRequestException(
                    "Job tidak bisa diambil dari status " + order.getStatus().getLabel());
        }

        order.setStatus(OrderStatus.SEDANG_DIKIRIM);
        orderRepository.save(order);

        orderStatusHistoryRepository.save(OrderStatusHistory.builder()
                .order(order)
                .status(OrderStatus.SEDANG_DIKIRIM)
                .note("Pesanan diambil oleh driver")
                .build());

        job.setDriverId(driverId);
        job.setTakenAt(LocalDateTime.now());
        DeliveryJob saved = deliveryJobRepository.save(job);

        return toDetail(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryJobSummaryResponse> getActiveJobs(UUID driverId) {
        log.info("[getActiveJobs] driverId={}", driverId);

        List<DeliveryJob> jobs = deliveryJobRepository
                .findByDriverIdAndOrder_StatusInOrderByTakenAtDesc(driverId,
                        List.of(OrderStatus.SEDANG_DIKIRIM));

        return jobs.stream().map(this::toSummary).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryJobSummaryResponse> getJobHistory(UUID driverId) {
        log.info("[getJobHistory] driverId={}", driverId);

        List<DeliveryJob> jobs = deliveryJobRepository
                .findByDriverIdAndOrder_StatusOrderByCompletedAtDesc(driverId, OrderStatus.SELESAI);

        return jobs.stream().map(this::toSummary).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DriverIncomeReportResponse getDriverReport(UUID driverId) {
        log.info("[getDriverReport] driverId={}", driverId);

        int totalJobsTaken = (int) deliveryJobRepository.countByDriverIdAndOrder_Status(driverId, OrderStatus.SELESAI);

        List<DeliveryJob> completed = deliveryJobRepository
                .findByDriverIdAndOrder_StatusOrderByCompletedAtDesc(driverId, OrderStatus.SELESAI);

        long totalIncome = completed.stream()
                .mapToLong(j -> j.getOrder().getDeliveryFee())
                .sum();

        List<DeliveryJobSummaryResponse> recent = completed.stream()
                .limit(10)
                .map(this::toSummary)
                .toList();

        List<DeliveryJob> activeJobsList = deliveryJobRepository
                .findByDriverIdAndOrder_StatusInOrderByTakenAtDesc(driverId,
                        List.of(OrderStatus.SEDANG_DIKIRIM));

        return DriverIncomeReportResponse.builder()
                .totalJobsTaken(totalJobsTaken)
                .completedJobs(completed.size())
                .activeJobs(activeJobsList.size())
                .totalIncome(totalIncome)
                .totalDeliveryFee(totalIncome)
                .recentCompletedJobs(recent)
                .build();
    }

    private DeliveryJobSummaryResponse toSummary(DeliveryJob job) {
        Order order = job.getOrder();
        return DeliveryJobSummaryResponse.builder()
                .deliveryJobId(job.getId())
                .orderId(order.getId())
                .storeId(order.getStoreId())
                .storeName(order.getStoreName())
                .recipientName(order.getRecipientName())
                .city(order.getCity())
                .postalCode(order.getPostalCode())
                .deliveryMethod(order.getDeliveryMethod())
                .deliveryMethodLabel(order.getDeliveryMethod().getLabel())
                .itemCount(order.getItems().size())
                .deliveryFee(order.getDeliveryFee())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .statusLabel(order.getStatus().getLabel())
                .availableSince(job.getCreatedAt())
                .build();
    }

    private DeliveryJobDetailResponse toDetail(DeliveryJob job) {
        Order order = job.getOrder();

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

        return DeliveryJobDetailResponse.builder()
                .deliveryJobId(job.getId())
                .orderId(order.getId())
                .buyerUsername(order.getBuyerUsername())
                .storeId(order.getStoreId())
                .storeName(order.getStoreName())
                .shippingAddress(shippingAddress)
                .deliveryMethod(order.getDeliveryMethod())
                .deliveryMethodLabel(order.getDeliveryMethod().getLabel())
                .status(order.getStatus())
                .statusLabel(order.getStatus().getLabel())
                .subtotal(order.getSubtotal())
                .deliveryFee(order.getDeliveryFee())
                .taxRatePercent(order.getTaxRatePercent())
                .taxAmount(order.getTaxAmount())
                .totalAmount(order.getTotalAmount())
                .items(items)
                .statusHistory(history)
                .availableSince(job.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public DeliveryJobDetailResponse completeJob(UUID jobId, UUID driverId) {
        log.info("[completeJob] jobId={} driverId={}", jobId, driverId);

        DeliveryJob job = deliveryJobRepository
                .findByIdAndDriverIdAndOrder_Status(jobId, driverId, OrderStatus.SEDANG_DIKIRIM)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Active delivery job not found or not owned by this driver"));

        Order order = job.getOrder();
        if (!order.getStatus().canTransitionTo(OrderStatus.SELESAI)) {
            throw new BadRequestException(
                    "Pesanan tidak bisa diselesaikan dari status " + order.getStatus().getLabel());
        }

        order.setStatus(OrderStatus.SELESAI);
        orderRepository.save(order);

        orderStatusHistoryRepository.save(OrderStatusHistory.builder()
                .order(order)
                .status(OrderStatus.SELESAI)
                .note("Pesanan selesai diantarkan oleh driver")
                .build());

        job.setCompletedAt(LocalDateTime.now());
        DeliveryJob saved = deliveryJobRepository.save(job);

        // Credit seller wallet
        storeRepository.findById(order.getStoreId()).ifPresent(store -> {
            walletService.creditBalance(store.getOwnerId(), order.getTotalAmount(),
                    "Pendapatan dari pesanan #" + order.getId().toString().substring(0, 8));
        });

        // Credit driver wallet
        long deliveryFee = order.getDeliveryMethod().getFee();
        walletService.creditBalance(job.getDriverId(), deliveryFee,
                "Pendapatan pengiriman #" + order.getId().toString().substring(0, 8));

        return toDetail(saved);
    }
}