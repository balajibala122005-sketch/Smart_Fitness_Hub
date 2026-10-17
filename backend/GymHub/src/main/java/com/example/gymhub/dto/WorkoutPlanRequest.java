package com.example.gymhub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class WorkoutPlanRequest {

    @NotBlank(message = "Plan name is required")
    @Size(max = 100, message = "Plan name cannot exceed 100 characters")
    private String planName;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    public WorkoutPlanRequest() {
    }

    public WorkoutPlanRequest(String planName, String description) {
        this.planName = planName;
        this.description = description;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
