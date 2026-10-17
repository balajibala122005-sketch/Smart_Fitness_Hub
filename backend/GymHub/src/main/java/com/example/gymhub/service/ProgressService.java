package com.example.gymhub.service;

import com.example.gymhub.dto.BodyMeasurementRequest;
import com.example.gymhub.dto.BodyMeasurementResponse;
import com.example.gymhub.dto.WorkoutLogRequest;
import com.example.gymhub.dto.WorkoutLogResponse;
import com.example.gymhub.dto.WorkoutStatsResponse;
import com.example.gymhub.entity.BodyMeasurement;
import com.example.gymhub.entity.User;
import com.example.gymhub.entity.UserWorkoutPlan;
import com.example.gymhub.entity.WorkoutLog;
import com.example.gymhub.repository.BodyMeasurementRepository;
import com.example.gymhub.repository.UserRepository;
import com.example.gymhub.repository.UserWorkoutPlanRepository;
import com.example.gymhub.repository.WorkoutLogRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProgressService {

    private final UserRepository userRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final BodyMeasurementRepository bodyMeasurementRepository;
    private final UserWorkoutPlanRepository workoutPlanRepository;

    public ProgressService(UserRepository userRepository,
                           WorkoutLogRepository workoutLogRepository,
                           BodyMeasurementRepository bodyMeasurementRepository,
                           UserWorkoutPlanRepository workoutPlanRepository) {
        this.userRepository = userRepository;
        this.workoutLogRepository = workoutLogRepository;
        this.bodyMeasurementRepository = bodyMeasurementRepository;
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
    public WorkoutLogResponse saveWorkoutLog(String email, WorkoutLogRequest request) {
        User user = getAuthenticatedUser(email);

        LocalDate logDate = request.getWorkoutDate() != null ? request.getWorkoutDate() : LocalDate.now();

        WorkoutLog log = new WorkoutLog(
                user,
                request.getWorkoutName(),
                request.getExerciseName().trim(),
                request.getExerciseType(),
                request.getSets(),
                request.getReps(),
                request.getDurationMinutes(),
                request.getCaloriesBurned(),
                request.getWeightUsed(),
                request.getNotes(),
                logDate
        );

        WorkoutLog saved = workoutLogRepository.save(log);
        return WorkoutLogResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<WorkoutLogResponse> getWorkoutLogs(String email) {
        User user = getAuthenticatedUser(email);
        List<WorkoutLog> logs = workoutLogRepository.findByUserOrderByWorkoutDateDescCreatedAtDesc(user);
        return logs.stream().map(WorkoutLogResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WorkoutStatsResponse getWorkoutStats(String email) {
        User user = getAuthenticatedUser(email);
        List<WorkoutLog> logs = workoutLogRepository.findByUserOrderByWorkoutDateDescCreatedAtDesc(user);
        long totalWorkouts = logs.size();
        long totalDuration = logs.stream().mapToLong(l -> l.getDurationMinutes() != null ? l.getDurationMinutes() : 0).sum();
        long totalCalories = logs.stream().mapToLong(l -> l.getCaloriesBurned() != null ? l.getCaloriesBurned() : 0).sum();
        double avgDuration = totalWorkouts > 0 ? Math.round((double) totalDuration / totalWorkouts * 10.0) / 10.0 : 0.0;
        LocalDate lastWorkoutDate = logs.isEmpty() ? null : logs.get(0).getWorkoutDate();
        String activePlan = workoutPlanRepository.findByUser(user)
                .map(UserWorkoutPlan::getPlanName)
                .orElse("Rank E: Novice");
        return new WorkoutStatsResponse(totalWorkouts, totalDuration, totalCalories, avgDuration, lastWorkoutDate, activePlan);
    }

    @Transactional
    public BodyMeasurementResponse saveMeasurement(String email, BodyMeasurementRequest request) {
        User user = getAuthenticatedUser(email);

        LocalDate measurementDate = request.getMeasurementDate() != null ? request.getMeasurementDate() : LocalDate.now();

        BodyMeasurement measurement = new BodyMeasurement(
                user,
                request.getWeight(),
                request.getChest(),
                request.getHeight() != null ? request.getHeight() : user.getHeight(),
                measurementDate
        );

        // Also update user's profile weight if height was provided
        user.setWeight(request.getWeight());
        if (request.getHeight() != null && request.getHeight() > 0) {
            user.setHeight(request.getHeight());
        }
        userRepository.save(user);

        BodyMeasurement saved = bodyMeasurementRepository.save(measurement);
        return BodyMeasurementResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<BodyMeasurementResponse> getMeasurements(String email) {
        User user = getAuthenticatedUser(email);
        List<BodyMeasurement> list = bodyMeasurementRepository.findByUserOrderByMeasurementDateAscCreatedAtAsc(user);
        return list.stream().map(BodyMeasurementResponse::fromEntity).collect(Collectors.toList());
    }
}
