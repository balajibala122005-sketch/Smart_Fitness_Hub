package com.example.gymhub.controller;

import com.example.gymhub.dto.DailyNutritionSummaryResponse;
import com.example.gymhub.dto.MealLogRequest;
import com.example.gymhub.dto.MealLogResponse;
import com.example.gymhub.service.NutritionService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/nutrition")
public class NutritionController {

    private final NutritionService nutritionService;

    public NutritionController(NutritionService nutritionService) {
        this.nutritionService = nutritionService;
    }

    @PostMapping("/meals")
    public ResponseEntity<MealLogResponse> logMeal(@Valid @RequestBody MealLogRequest request,
                                                   Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        MealLogResponse response = nutritionService.saveMeal(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/meals")
    public ResponseEntity<List<MealLogResponse>> getMeals(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<MealLogResponse> list = nutritionService.getMeals(authentication.getName(), date);
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/meals/{id}")
    public ResponseEntity<Map<String, Object>> deleteMeal(
            @PathVariable Long id,
            Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        nutritionService.deleteMeal(authentication.getName(), id);
        return ResponseEntity.ok(Map.of(
                "message", "Meal log deleted successfully",
                "id", id
        ));
    }

    @GetMapping("/summary")
    public ResponseEntity<DailyNutritionSummaryResponse> getDailySummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        DailyNutritionSummaryResponse summary = nutritionService.getDailySummary(authentication.getName(), date);
        return ResponseEntity.ok(summary);
    }
}
