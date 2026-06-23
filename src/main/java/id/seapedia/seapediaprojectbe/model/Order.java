package id.seapedia.seapediaprojectbe.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "buyer_id", nullable = false)
    private UUID buyerId;

    @Column(name = "buyer_username", nullable = false)
    private String buyerUsername;

    @Column(name = "store_id", nullable = false)
    private UUID storeId;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(name = "address_id", nullable = false)
    private UUID addressId;

    @Column(name = "recipient_name", nullable = false)
    private String recipientName;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String fullAddress;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String postalCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_method", nullable = false)
    private DeliveryMethod deliveryMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private Long subtotal;

    /**
     * Kode diskon yang dipakai (Voucher/Promo), snapshot apa adanya saat order dibuat.
     * Null jika tidak pakai diskon.
     */
    @Column(name = "discount_code")
    private String discountCode;

    /**
     * Membedakan secara eksplisit apakah discountCode berasal dari Voucher atau Promo.
     * NONE jika tidak ada diskon dipakai.
     */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "discount_source", nullable = false)
    private DiscountSource discountSource = DiscountSource.NONE;

    /**
     * Nominal potongan dari diskon (dipotong dari subtotal, SEBELUM PPN dihitung).
     */
    @Builder.Default
    @Column(name = "discount_amount", nullable = false)
    private Long discountAmount = 0L;

    @Column(name = "delivery_fee", nullable = false)
    private Long deliveryFee;

    @Column(name = "tax_rate_percent", nullable = false)
    private Integer taxRatePercent;

    /**
     * Dasar pengenaan PPN = subtotal - discountAmount (delivery fee TIDAK dikenai PPN).
     * Lihat dokumentasi posisi diskon vs PPN di OrderServiceImpl.
     */
    @Column(name = "tax_base", nullable = false)
    private Long taxBase;

    @Column(name = "tax_amount", nullable = false)
    private Long taxAmount;

    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    @Column(name = "wallet_balance_before", nullable = false)
    private Long walletBalanceBefore;

    @Column(name = "wallet_balance_after", nullable = false)
    private Long walletBalanceAfter;

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> items = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderStatusHistory> statusHistories = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
