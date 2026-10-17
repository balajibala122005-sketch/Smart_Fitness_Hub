package com.example.gymhub.dto;

import com.example.gymhub.entity.WorkoutLog;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class WorkoutLogResponse {

    private Long id;
    private String workoutName;
    private String exerciseName;
    private String exerciseType;
    private Integer sets;
    private Integer reps;
    private Integer durationMinutes;
    private Integer caloriesBurned;
    private Double weightUsed;
    private String notes;
    private LocalDate workoutDate;
    private LocalDateTime createdAt;

    public WorkoutLogResponse() {
    }

    public static WorkoutLogResponse fromEntity(WorkoutLog log) {
        if (log == null) {
            return null;
        }
        WorkoutLogResponse res = new WorkoutLogResponse();
        res.setId(log.getId());
        res.setWorkoutName(log.getWorkoutName());
        res.setExerciseName(log.getExerciseName());
        res.setExerciseType(log.getExerciseType() != null ? log.getExerciseType() : log.getExerciseName());
        res.setSets(log.getSets());
        res.setReps(log.getReps());
        res.setDurationMinutes(log.getDurationMinutes());
        res.setCaloriesBurned(log.getCaloriesBurned());
        res.setWeightUsed(log.getWeightUsed());
        res.setNotes(log.getNotes());
        res.setWorkoutDate(log.getWorkoutDate());
        res.setCreatedAt(log.getCreatedAt());
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
