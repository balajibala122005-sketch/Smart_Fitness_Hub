package com.example.gymhub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "meal_logs")
public class MealLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Meal name is required")
    @Column(nullable = false)
    private String mealName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MealType mealType;

    @NotNull(message = "Meal date is required")
    @Column(nullable = false)
    private LocalDate mealDate = LocalDate.now();

    @NotNull(message = "Calories are required")
    @Min(value = 0, message = "Calories cannot be negative")
    @Max(value = 10000, message = "Calories cannot exceed 10000 kcal")
    @Column(nullable = false)
    private Integer calories;

    @Min(value = 0, message = "Protein cannot be negative")
    @Max(value = 1000, message = "Protein cannot exceed 1000 grams")
    @Column(nullable = false)
    private Double protein = 0.0;

    @Min(value = 0, message = "Carbohydrates cannot be negative")
    @Max(value = 1000, message = "Carbohydrates cannot exceed 1000 grams")
    @Column(nullable = false)
    private Double carbohydrates = 0.0;

    @Min(value = 0, message = "Fats cannot be negative")
    @Max(value = 1000, message = "Fats cannot exceed 1000 grams")
    @Column(nullable = false)
    private Double fats = 0.0;

    @Column(length = 1000)
    private String notes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public MealLog() {
    }

    public MealLog(User user, String mealName, MealType mealType, LocalDate mealDate, Integer calories, Double protein, Double carbohydrates, Double fats, String notes) {
        this.user = user;
        this.mealName = mealName;
        this.mealType = mealType;
        this.mealDate = mealDate != null ? mealDate : LocalDate.now();
        this.calories = calories;
        this.protein = protein != null ? protein : 0.0;
        this.carbohydrates = carbohydrates != null ? carbohydrates : 0.0;
        this.fats = fats != null ? fats : 0.0;
        this.notes = notes;
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

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public MealType getMealType() {
        return mealType;
    }

    public void setMealType(MealType mealType) {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
