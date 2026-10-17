package com.example.gymhub.controller;

import com.example.gymhub.dto.WorkoutPlanRequest;
import com.example.gymhub.dto.WorkoutPlanResponse;
import com.example.gymhub.service.WorkoutService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @PostMapping("/plan")
    public ResponseEntity<WorkoutPlanResponse> savePlan(@Valid @RequestBody WorkoutPlanRequest request,
                                                        Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        WorkoutPlanResponse response = workoutService.saveOrUpdatePlan(authentication.getName(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/plan")
    public ResponseEntity<WorkoutPlanResponse> getPlan(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        WorkoutPlanResponse plan = workoutService.getUserPlan(authentication.getName());
        return ResponseEntity.ok(plan);
    }
}
