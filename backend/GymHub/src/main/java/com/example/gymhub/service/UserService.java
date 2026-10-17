package com.example.gymhub.service;

import com.example.gymhub.dto.DashboardSummaryDto;
import com.example.gymhub.dto.UserDto;
import com.example.gymhub.entity.BodyMeasurement;
import com.example.gymhub.entity.User;
import com.example.gymhub.entity.UserWorkoutPlan;
import com.example.gymhub.repository.BodyMeasurementRepository;
import com.example.gymhub.repository.MealLogRepository;
import com.example.gymhub.repository.UserRepository;
import com.example.gymhub.repository.UserWorkoutPlanRepository;
import com.example.gymhub.repository.WorkoutLogRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final UserWorkoutPlanRepository workoutPlanRepository;
    private final BodyMeasurementRepository bodyMeasurementRepository;
    private final MealLogRepository mealLogRepository;

    public UserService(UserRepository userRepository,
                       WorkoutLogRepository workoutLogRepository,
                       UserWorkoutPlanRepository workoutPlanRepository,
                       BodyMeasurementRepository bodyMeasurementRepository,
                       MealLogRepository mealLogRepository) {
        this.userRepository = userRepository;
        this.workoutLogRepository = workoutLogRepository;
        this.workoutPlanRepository = workoutPlanRepository;
        this.bodyMeasurementRepository = bodyMeasurementRepository;
        this.mealLogRepository = mealLogRepository;
    }

    @Transactional(readOnly = true)
    public UserDto getUserProfile(String email) {
        User user = findUserByEmail(email);
        return UserDto.fromEntity(user);
    }

    @Transactional(readOnly = true)
    public DashboardSummaryDto getDashboardSummary(String email) {
        User user = findUserByEmail(email);
        DashboardSummaryDto summary = DashboardSummaryDto.fromUser(user);

        // Inject real count of logged workouts
        long totalWorkouts = workoutLogRepository.countByUser(user);
        summary.setTotalWorkouts((int) totalWorkouts);

        // Inject real active plan if saved
        workoutPlanRepository.findByUser(user)
                .ifPresent(plan -> summary.setActivePlan(plan.getPlanName()));

        // Inject real daily nutrition calories for today
        int todayCalories = mealLogRepository.findByUserAndMealDateOrderByCreatedAtDesc(user, LocalDate.now())
                .stream()
                .mapToInt(m -> m.getCalories() != null ? m.getCalories() : 0)
                .sum();
        summary.setTodayCalories(todayCalories);

        // If latest measurement has updated weight, use it to recalculate latest BMI
        bodyMeasurementRepository.findTopByUserOrderByMeasurementDateDescCreatedAtDesc(user)
                .ifPresent(m -> {
                    summary.setWeight(m.getWeight());
                    if (summary.getHeight() != null && summary.getHeight() > 0) {
                        double heightM = summary.getHeight() / 100.0;
                        double rawBmi = m.getWeight() / (heightM * heightM);
                        double roundedBmi = Math.round(rawBmi * 10.0) / 10.0;
                        summary.setBmi(roundedBmi);
                        if (roundedBmi < 18.5) {
                            summary.setBmiStatus("Under Threshold");
                        } else if (roundedBmi < 24.9) {
                            summary.setBmiStatus("Optimal Condition");
                        } else if (roundedBmi < 29.9) {
                            summary.setBmiStatus("Above Baseline");
                        } else {
                            summary.setBmiStatus("Critical Mass");
                        }
                        summary.setTargetCalories((int) Math.round(m.getWeight() * 30));
                    }
                });

        return summary;
    }

    private User findUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new BadCredentialsException("Unauthenticated request");
        }
        return userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new BadCredentialsException("User not found: " + email));
    }
}
