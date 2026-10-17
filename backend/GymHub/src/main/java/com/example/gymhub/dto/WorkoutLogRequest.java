package com.example.gymhub.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class WorkoutLogRequest {

    @Size(max = 100, message = "Workout name cannot exceed 100 characters")
    private String workoutName;

    @NotBlank(message = "Exercise name or exercise type is required")
    @Size(max = 100, message = "Exercise name cannot exceed 100 characters")
    private String exerciseName;

    @Size(max = 100, message = "Exercise type cannot exceed 100 characters")
    @JsonAlias({"type", "exercise_type"})
    private String exerciseType;

    @Min(value = 1, message = "Sets must be at least 1")
    @Max(value = 100, message = "Sets cannot exceed 100")
    private Integer sets;

    @Min(value = 1, message = "Reps must be at least 1")
    @Max(value = 1000, message = "Reps cannot exceed 1000")
    private Integer reps;

    @NotNull(message = "Duration in minutes is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 1440, message = "Duration cannot exceed 1440 minutes")
    private Integer durationMinutes;

    @Min(value = 0, message = "Calories burned cannot be negative")
    @Max(value = 10000, message = "Calories burned cannot exceed 10000")
    private Integer caloriesBurned;

    @Min(value = 0, message = "Weight used cannot be negative")
    @Max(value = 1000, message = "Weight used cannot exceed 1000 kg")
    private Double weightUsed;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;

    @PastOrPresent(message = "Workout date cannot be in the future")
    private LocalDate workoutDate;

    public WorkoutLogRequest() {
    }

    public WorkoutLogRequest(String workoutName, String exerciseName, Integer sets, Integer reps, Integer durationMinutes, Double weightUsed, LocalDate workoutDate) {
        this.workoutName = workoutName;
        this.exerciseName = exerciseName;
        this.exerciseType = exerciseName;
        this.sets = sets;
        this.reps = reps;
        this.durationMinutes = durationMinutes;
        this.weightUsed = weightUsed;
        this.workoutDate = workoutDate;
    }

    public WorkoutLogRequest(String workoutName, String exerciseName, String exerciseType, Integer sets, Integer reps, Integer durationMinutes, Integer caloriesBurned, Double weightUsed, String notes, LocalDate workoutDate) {
        this.workoutName = workoutName;
        this.exerciseName = exerciseName != null ? exerciseName : exerciseType;
        this.exerciseType = exerciseType != null ? exerciseType : exerciseName;
        this.sets = sets;
        this.reps = reps;
        this.durationMinutes = durationMinutes;
        this.caloriesBurned = caloriesBurned;
        this.weightUsed = weightUsed;
        this.notes = notes;
        this.workoutDate = workoutDate;
    }

    public WorkoutLogRequest(String workoutName, String exerciseType, Integer durationMinutes, Integer caloriesBurned, LocalDate workoutDate, String notes) {
        this.workoutName = workoutName;
        this.exerciseType = exerciseType;
        this.exerciseName = exerciseType;
        this.durationMinutes = durationMinutes;
        this.caloriesBurned = caloriesBurned;
        this.workoutDate = workoutDate;
        this.notes = notes;
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
        if ((this.exerciseType == null || this.exerciseType.trim().isEmpty()) && exerciseName != null && !exerciseName.trim().isEmpty()) {
            this.exerciseType = exerciseName;
        }
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
        if ((this.exerciseName == null || this.exerciseName.trim().isEmpty()) && exerciseType != null && !exerciseType.trim().isEmpty()) {
            this.exerciseName = exerciseType;
        }
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

    public Integer getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(Integer caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public Double getWeightUsed() {
        return weightUsed;
    }

    public void setWeightUsed(Double weightUsed) {
        this.weightUsed = weightUsed;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getWorkoutDate() {
        return workoutDate;
    }

    public void setWorkoutDate(LocalDate workoutDate) {
        this.workoutDate = workoutDate;
    }
}
