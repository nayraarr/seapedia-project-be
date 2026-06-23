package id.seapedia.seapediaprojectbe.controller;

import id.seapedia.seapediaprojectbe.dto.common.ApiResponse;
import id.seapedia.seapediaprojectbe.service.SimulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/simulate")
@RequiredArgsConstructor
public class SimulationController {

    private final SimulationService simulationService;

    @PostMapping("/advance")
    public ResponseEntity<ApiResponse<Map<String, Object>>> advance(@RequestParam long minutes) {
        simulationService.advance(minutes);
        return ResponseEntity.ok(ApiResponse.success("Time advanced", statusMap()));
    }

    @PostMapping("/reset")
    public ResponseEntity<ApiResponse<Map<String, Object>>> reset() {
        simulationService.reset();
        return ResponseEntity.ok(ApiResponse.success("Simulation reset", statusMap()));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> status() {
        return ResponseEntity.ok(ApiResponse.success("Simulation status", statusMap()));
    }

    private Map<String, Object> statusMap() {
        return Map.of(
                "offsetMinutes", simulationService.getOffsetMinutes(),
                "simulatedNow", simulationService.now().toString()
        );
    }
}
