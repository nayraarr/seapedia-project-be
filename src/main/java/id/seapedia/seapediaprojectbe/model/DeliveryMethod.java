package id.seapedia.seapediaprojectbe.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeliveryMethod {
    INSTANT("Instant", 25000L),
    NEXT_DAY("Next Day", 15000L),
    REGULAR("Regular", 10000L);

    private final String label;
    private final Long fee;
}
