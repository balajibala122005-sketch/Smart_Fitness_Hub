package com.example.gymhub.controller;

import com.example.gymhub.dto.BodyMeasurementRequest;
import com.example.gymhub.dto.BodyMeasurementResponse;
import com.example.gymhub.dto.WorkoutLogRequest;
import com.example.gymhub.dto.WorkoutLogResponse;
import com.example.gymhub.dto.WorkoutStatsResponse;
import com.example.gymhub.service.ProgressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @PostMapping("/workouts")
    public ResponseEntity<WorkoutLogResponse> logWorkout(@Valid @RequestBody WorkoutLogRequest request,
                                                         Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        WorkoutLogResponse response = progressService.saveWorkoutLog(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/workouts")
    public ResponseEntity<List<WorkoutLogResponse>> getWorkouts(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<WorkoutLogResponse> logs = progressService.getWorkoutLogs(authentication.getName());
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/workouts/stats")
    public ResponseEntity<WorkoutStatsResponse> getWorkoutStats(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        WorkoutStatsResponse stats = progressService.getWorkoutStats(authentication.getName());
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/measurements")
    public ResponseEntity<BodyMeasurementResponse> logMeasurement(@Valid @RequestBody BodyMeasurementRequest request,
                                                                 Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        BodyMeasurementResponse response = progressService.saveMeasurement(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/measurements")
    public ResponseEntity<List<BodyMeasurementResponse>> getMeasurements(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<BodyMeasurementResponse> list = progressService.getMeasurements(authentication.getName());
        return ResponseEntity.ok(list);
    }
}
