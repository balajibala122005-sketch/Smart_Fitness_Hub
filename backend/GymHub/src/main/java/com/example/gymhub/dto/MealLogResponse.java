package com.example.gymhub.dto;

import com.example.gymhub.entity.MealLog;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MealLogResponse {

    private Long id;
    private String mealName;
    private String mealType;
    private LocalDate mealDate;
    private Integer calories;
    private Double protein;
    private Double carbohydrates;
    private Double fats;
    private String notes;
    private LocalDateTime createdAt;

    public MealLogResponse() {
    }

    public static MealLogResponse fromEntity(MealLog log) {
        if (log == null) {
            return null;
        }
        MealLogResponse res = new MealLogResponse();
        res.setId(log.getId());
        res.setMealName(log.getMealName());
        res.setMealType(log.getMealType() != null ? log.getMealType().name() : null);
        res.setMealDate(log.getMealDate());
        res.setCalories(log.getCalories());
        res.setProtein(log.getProtein());
        res.setCarbohydrates(log.getCarbohydrates());
        res.setFats(log.getFats());
        res.setNotes(log.getNotes());
        res.setCreatedAt(log.getCreatedAt());
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public LocalDate getMealDate() {
        return mealDate;
    }

    public void setMealDate(LocalDate mealDate) {
        this.mealDate = mealDate;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public Double getProtein() {
        return protein;
    }

    public void setProtein(Double protein) {
        this.protein = protein;
    }

    public Double getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(Double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public Double getFats() {
        return fats;
    }

    public void setFats(Double fats) {
        this.fats = fats;
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
