package com.example.gymhub.dto;

import java.time.LocalDate;

public class DailyNutritionSummaryResponse {

    private LocalDate date;
    private int totalCalories;
    private double totalProtein;
    private double totalCarbohydrates;
    private double totalFats;
    private int mealCount;
    private Integer targetCalories;

    public DailyNutritionSummaryResponse() {
    }

    public DailyNutritionSummaryResponse(LocalDate date, int totalCalories, double totalProtein, double totalCarbohydrates, double totalFats, int mealCount, Integer targetCalories) {
        this.date = date;
        this.totalCalories = totalCalories;
        this.totalProtein = Math.round(totalProtein * 10.0) / 10.0;
        this.totalCarbohydrates = Math.round(totalCarbohydrates * 10.0) / 10.0;
        this.totalFats = Math.round(totalFats * 10.0) / 10.0;
        this.mealCount = mealCount;
        this.targetCalories = targetCalories;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(int totalCalories) {
        this.totalCalories = totalCalories;
    }

    public double getTotalProtein() {
        return totalProtein;
    }

    public void setTotalProtein(double totalProtein) {
        this.totalProtein = Math.round(totalProtein * 10.0) / 10.0;
    }

    public double getTotalCarbohydrates() {
        return totalCarbohydrates;
    }

    public void setTotalCarbohydrates(double totalCarbohydrates) {
        this.totalCarbohydrates = Math.round(totalCarbohydrates * 10.0) / 10.0;
    }

    public double getTotalFats() {
        return totalFats;
    }

    public void setTotalFats(double totalFats) {
        this.totalFats = Math.round(totalFats * 10.0) / 10.0;
    }

    public int getMealCount() {
        return mealCount;
    }

    public void setMealCount(int mealCount) {
        this.mealCount = mealCount;
    }

    public Integer getTargetCalories() {
        return targetCalories;
    }

    public void setTargetCalories(Integer targetCalories) {
        this.targetCalories = targetCalories;
    }
}
