package id.seapedia.seapediaprojectbe.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeliveryMethod {
    INSTANT("Instant",   25000L, 120L),
    NEXT_DAY("Next Day", 15000L, 1800L),
    REGULAR("Regular",   10000L, 10080L);

    private final String label;
    private final Long fee;
    private final long slaSinceCreatedMinutes;

    public long getSlaSinceCreatedMinutes() {
        return slaSinceCreatedMinutes;
    }
}
