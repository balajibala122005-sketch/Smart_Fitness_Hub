package com.example.gymhub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "workout_logs")
public class WorkoutLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String workoutName;

    private String exerciseType;

    @NotBlank(message = "Exercise name is required")
    @Column(nullable = false)
    private String exerciseName;

    @Min(value = 1, message = "Sets must be at least 1")
    @Column(nullable = true)
    private Integer sets;

    @Min(value = 1, message = "Reps must be at least 1")
    @Column(nullable = true)
    private Integer reps;

    @NotNull(message = "Duration in minutes is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Column(nullable = false)
    private Integer durationMinutes;

    private Integer caloriesBurned;

    private Double weightUsed;

    @Column(length = 1000)
    private String notes;

    @Column(nullable = false)
    private LocalDate workoutDate = LocalDate.now();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public WorkoutLog() {
    }

    public WorkoutLog(User user, String workoutName, String exerciseName, Integer sets, Integer reps, Integer durationMinutes, Double weightUsed, LocalDate workoutDate) {
        this(user, workoutName, exerciseName, "Strength", sets, reps, durationMinutes, null, weightUsed, null, workoutDate);
    }

    public WorkoutLog(User user, String workoutName, String exerciseName, String exerciseType, Integer sets, Integer reps, Integer durationMinutes, Integer caloriesBurned, Double weightUsed, String notes, LocalDate workoutDate) {
        this.user = user;
        this.workoutName = workoutName != null ? workoutName : "Workout Routine";
        this.exerciseName = exerciseName;
        this.exerciseType = exerciseType != null ? exerciseType : "General";
        this.sets = sets;
        this.reps = reps;
        this.durationMinutes = durationMinutes;
        this.caloriesBurned = caloriesBurned;
        this.weightUsed = weightUsed;
        this.notes = notes;
        this.workoutDate = workoutDate != null ? workoutDate : LocalDate.now();
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public Integer getSets() {
        return sets;
    }

    public void setSets(Integer sets) {
        this.sets = sets;
    }

    public Integer getReps() {
        return reps;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Double getWeightUsed() {
        return weightUsed;
    }

    public void setWeightUsed(Double weightUsed) {
        this.weightUsed = weightUsed;
    }

    public LocalDate getWorkoutDate() {
        return workoutDate;
    }

    public void setWorkoutDate(LocalDate workoutDate) {
        this.workoutDate = workoutDate;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    public Integer getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(Integer caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
