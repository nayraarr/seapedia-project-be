package id.seapedia.seapediaprojectbe.service;

import java.time.LocalDateTime;

public interface SimulationService {
    LocalDateTime now();
    long getOffsetMinutes();
    void advance(long minutes);
    void reset();
}
