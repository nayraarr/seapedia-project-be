package id.seapedia.seapediaprojectbe.service.impl;

import id.seapedia.seapediaprojectbe.service.SimulationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SimulationServiceImpl implements SimulationService {

    private long offsetMinutes = 0;

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now().plusMinutes(offsetMinutes);
    }

    @Override
    public long getOffsetMinutes() {
        return offsetMinutes;
    }

    @Override
    public void advance(long minutes) {
        this.offsetMinutes += minutes;
    }

    @Override
    public void reset() {
        this.offsetMinutes = 0;
    }
}
