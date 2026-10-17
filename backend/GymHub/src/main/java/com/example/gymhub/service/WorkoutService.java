package com.example.gymhub.service;

import com.example.gymhub.dto.WorkoutPlanRequest;
import com.example.gymhub.dto.WorkoutPlanResponse;
import com.example.gymhub.entity.User;
import com.example.gymhub.entity.UserWorkoutPlan;
import com.example.gymhub.repository.UserRepository;
import com.example.gymhub.repository.UserWorkoutPlanRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class WorkoutService {

    private final UserRepository userRepository;
    private final UserWorkoutPlanRepository workoutPlanRepository;

    public WorkoutService(UserRepository userRepository, UserWorkoutPlanRepository workoutPlanRepository) {
        this.userRepository = userRepository;
        this.workoutPlanRepository = workoutPlanRepository;
    }

    private User getAuthenticatedUser(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new BadCredentialsException("Unauthenticated request");
        }
        return userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new BadCredentialsException("User not found: " + email));
    }

    @Transactional
    public WorkoutPlanResponse saveOrUpdatePlan(String email, WorkoutPlanRequest request) {
        User user = getAuthenticatedUser(email);

        UserWorkoutPlan plan = workoutPlanRepository.findByUser(user)
                .orElse(new UserWorkoutPlan());

        plan.setUser(user);
        plan.setPlanName(request.getPlanName().trim());
        plan.setDescription(request.getDescription());
        plan.setUpdatedAt(LocalDateTime.now());

        UserWorkoutPlan saved = workoutPlanRepository.save(plan);
        return WorkoutPlanResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public WorkoutPlanResponse getUserPlan(String email) {
        User user = getAuthenticatedUser(email);
        return workoutPlanRepository.findByUser(user)
                .map(WorkoutPlanResponse::fromEntity)
                .orElse(new WorkoutPlanResponse() {{
                    setPlanName("Rank E: Novice");
                    setDescription("Lay the foundation. Build core stability and master the basics.");
                    setDisclaimer("Suggestion only; not medical advice.");
                }});
    }
}
