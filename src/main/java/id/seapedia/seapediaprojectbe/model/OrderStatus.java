package id.seapedia.seapediaprojectbe.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.Set;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    SEDANG_DIKEMAS("Sedang Dikemas"),
    MENUNGGU_PENGIRIM("Menunggu Pengirim"),
    SEDANG_DIKIRIM("Sedang Dikirim"),
    SELESAI("Pesanan Selesai"),
    DIKEMBALIKAN("Dikembalikan");

    private final String label;

    private static final Map<OrderStatus, Set<OrderStatus>> VALID_TRANSITIONS = Map.of(
        SEDANG_DIKEMAS,    Set.of(MENUNGGU_PENGIRIM, DIKEMBALIKAN),
        MENUNGGU_PENGIRIM, Set.of(SEDANG_DIKIRIM, DIKEMBALIKAN),
        SEDANG_DIKIRIM,    Set.of(SELESAI, DIKEMBALIKAN),
        SELESAI,           Set.of(),
        DIKEMBALIKAN,      Set.of()
    );

    public boolean canTransitionTo(OrderStatus target) {
        return VALID_TRANSITIONS.getOrDefault(this, Set.of()).contains(target);
    }
}
