package com.example.gymhub.dto;

import java.time.LocalDate;

public class WorkoutStatsResponse {

    private long totalWorkouts;
    private long totalDurationMinutes;
    private long totalCaloriesBurned;
    private double averageDurationMinutes;
    private LocalDate lastWorkoutDate;
    private String activePlan;

    public WorkoutStatsResponse() {
    }

    public WorkoutStatsResponse(long totalWorkouts, long totalDurationMinutes, long totalCaloriesBurned, double averageDurationMinutes, LocalDate lastWorkoutDate, String activePlan) {
        this.totalWorkouts = totalWorkouts;
        this.totalDurationMinutes = totalDurationMinutes;
        this.totalCaloriesBurned = totalCaloriesBurned;
        this.averageDurationMinutes = averageDurationMinutes;
        this.lastWorkoutDate = lastWorkoutDate;
        this.activePlan = activePlan;
    }

    public long getTotalWorkouts() {
        return totalWorkouts;
    }

    public void setTotalWorkouts(long totalWorkouts) {
        this.totalWorkouts = totalWorkouts;
    }

    public long getTotalDurationMinutes() {
        return totalDurationMinutes;
    }

    public void setTotalDurationMinutes(long totalDurationMinutes) {
        this.totalDurationMinutes = totalDurationMinutes;
    }

    public long getTotalCaloriesBurned() {
        return totalCaloriesBurned;
    }

    public void setTotalCaloriesBurned(long totalCaloriesBurned) {
        this.totalCaloriesBurned = totalCaloriesBurned;
    }

    public double getAverageDurationMinutes() {
        return averageDurationMinutes;
    }

    public void setAverageDurationMinutes(double averageDurationMinutes) {
        this.averageDurationMinutes = averageDurationMinutes;
    }

    public LocalDate getLastWorkoutDate() {
        return lastWorkoutDate;
    }

    public void setLastWorkoutDate(LocalDate lastWorkoutDate) {
        this.lastWorkoutDate = lastWorkoutDate;
    }

    public String getActivePlan() {
        return activePlan;
    }

    public void setActivePlan(String activePlan) {
        this.activePlan = activePlan;
    }
}
