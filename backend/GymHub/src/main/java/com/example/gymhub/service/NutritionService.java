package com.example.gymhub.service;

import com.example.gymhub.dto.DailyNutritionSummaryResponse;
import com.example.gymhub.dto.MealLogRequest;
import com.example.gymhub.dto.MealLogResponse;
import com.example.gymhub.entity.MealLog;
import com.example.gymhub.entity.MealType;
import com.example.gymhub.entity.User;
import com.example.gymhub.exception.ResourceNotFoundException;
import com.example.gymhub.repository.MealLogRepository;
import com.example.gymhub.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NutritionService {

    private final UserRepository userRepository;
    private final MealLogRepository mealLogRepository;

    public NutritionService(UserRepository userRepository, MealLogRepository mealLogRepository) {
        this.userRepository = userRepository;
        this.mealLogRepository = mealLogRepository;
    }

    private User getAuthenticatedUser(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new BadCredentialsException("Unauthenticated request");
        }
        return userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new BadCredentialsException("User not found: " + email));
    }

    @Transactional
    public MealLogResponse saveMeal(String email, MealLogRequest request) {
        User user = getAuthenticatedUser(email);

        MealType mealType = MealType.fromString(request.getMealType());
        LocalDate logDate = request.getMealDate() != null ? request.getMealDate() : LocalDate.now();

        MealLog mealLog = new MealLog(
                user,
                request.getMealName().trim(),
                mealType,
                logDate,
                request.getCalories(),
                request.getProtein() != null ? request.getProtein() : 0.0,
                request.getCarbohydrates() != null ? request.getCarbohydrates() : 0.0,
                request.getFats() != null ? request.getFats() : 0.0,
                request.getNotes() != null ? request.getNotes().trim() : null
        );

        MealLog saved = mealLogRepository.save(mealLog);
        return MealLogResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<MealLogResponse> getMeals(String email, LocalDate date) {
        User user = getAuthenticatedUser(email);
        List<MealLog> list;
        if (date != null) {
            list = mealLogRepository.findByUserAndMealDateOrderByCreatedAtDesc(user, date);
        } else {
            list = mealLogRepository.findByUserOrderByMealDateDescCreatedAtDesc(user);
        }
        return list.stream().map(MealLogResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public void deleteMeal(String email, Long mealId) {
        User user = getAuthenticatedUser(email);
        MealLog meal = mealLogRepository.findByIdAndUser(mealId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Meal log not found or not authorized to delete with ID: " + mealId));
        mealLogRepository.delete(meal);
    }

    @Transactional(readOnly = true)
    public DailyNutritionSummaryResponse getDailySummary(String email, LocalDate date) {
        User user = getAuthenticatedUser(email);
        LocalDate targetDate = date != null ? date : LocalDate.now();

        List<MealLog> meals = mealLogRepository.findByUserAndMealDateOrderByCreatedAtDesc(user, targetDate);

        int totalCalories = 0;
        double totalProtein = 0.0;
        double totalCarbohydrates = 0.0;
        double totalFats = 0.0;

        for (MealLog meal : meals) {
            if (meal.getCalories() != null) totalCalories += meal.getCalories();
            if (meal.getProtein() != null) totalProtein += meal.getProtein();
            if (meal.getCarbohydrates() != null) totalCarbohydrates += meal.getCarbohydrates();
            if (meal.getFats() != null) totalFats += meal.getFats();
        }

        Integer targetCalories = null;
        if (user.getWeight() != null && user.getWeight() > 0) {
            targetCalories = (int) Math.round(user.getWeight() * 30);
        }

        return new DailyNutritionSummaryResponse(
                targetDate,
                totalCalories,
                totalProtein,
                totalCarbohydrates,
                totalFats,
                meals.size(),
                targetCalories
        );
    }
}
