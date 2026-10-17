package com.example.gymhub.entity;

public enum MealType {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK;

    public static MealType fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Meal type is required and must be one of: breakfast, lunch, dinner, snack");
        }
        try {
            return MealType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid meal type: '" + value + "'. Must be one of: breakfast, lunch, dinner, snack");
        }
    }
}
