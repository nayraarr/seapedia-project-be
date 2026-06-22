package id.seapedia.seapediaprojectbe.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    SEDANG_DIKEMAS("Sedang Dikemas"),
    DIPROSES("Diproses"),
    DIKIRIM("Dikirim"),
    SELESAI("Selesai"),
    DIBATALKAN("Dibatalkan");

    private final String label;
}
