package com.example.gymhub.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class MealLogRequest {

    @NotBlank(message = "Meal name is required")
    @Size(max = 100, message = "Meal name cannot exceed 100 characters")
    private String mealName;

    @NotBlank(message = "Meal type is required (breakfast, lunch, dinner, snack)")
    @JsonAlias({"type", "meal_type"})
    private String mealType;

    @PastOrPresent(message = "Meal date cannot be in the future")
    @JsonAlias({"date", "meal_date"})
    private LocalDate mealDate;

    @NotNull(message = "Calories are required")
    @Min(value = 0, message = "Calories cannot be negative")
    @Max(value = 10000, message = "Calories cannot exceed 10000 kcal")
    private Integer calories;

    @Min(value = 0, message = "Protein cannot be negative")
    @Max(value = 1000, message = "Protein cannot exceed 1000 grams")
    private Double protein = 0.0;

    @Min(value = 0, message = "Carbohydrates cannot be negative")
    @Max(value = 1000, message = "Carbohydrates cannot exceed 1000 grams")
    @JsonAlias({"carbs", "carb"})
    private Double carbohydrates = 0.0;

    @Min(value = 0, message = "Fats cannot be negative")
    @Max(value = 1000, message = "Fats cannot exceed 1000 grams")
    @JsonAlias({"fat"})
    private Double fats = 0.0;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;

    public MealLogRequest() {
    }

    public MealLogRequest(String mealName, String mealType, LocalDate mealDate, Integer calories, Double protein, Double carbohydrates, Double fats, String notes) {
        this.mealName = mealName;
        this.mealType = mealType;
        this.mealDate = mealDate;
        this.calories = calories;
        this.protein = protein != null ? protein : 0.0;
        this.carbohydrates = carbohydrates != null ? carbohydrates : 0.0;
        this.fats = fats != null ? fats : 0.0;
        this.notes = notes;
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
        this.protein = protein != null ? protein : 0.0;
    }

    public Double getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(Double carbohydrates) {
        this.carbohydrates = carbohydrates != null ? carbohydrates : 0.0;
    }

    public Double getFats() {
        return fats;
    }

    public void setFats(Double fats) {
        this.fats = fats != null ? fats : 0.0;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
