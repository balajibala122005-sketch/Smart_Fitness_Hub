package com.example.gymhub.dto;

import com.example.gymhub.entity.UserWorkoutPlan;
import java.time.LocalDateTime;

public class WorkoutPlanResponse {

    private Long id;
    private String planName;
    private String description;
    private LocalDateTime updatedAt;
    private String disclaimer = "Suggestion only; not medical advice.";

    public WorkoutPlanResponse() {
    }

    public static WorkoutPlanResponse fromEntity(UserWorkoutPlan plan) {
        if (plan == null) {
            return null;
        }
        WorkoutPlanResponse res = new WorkoutPlanResponse();
        res.setId(plan.getId());
        res.setPlanName(plan.getPlanName());
        res.setDescription(plan.getDescription());
        res.setUpdatedAt(plan.getUpdatedAt());
        res.setDisclaimer("Suggestion only; not medical advice.");
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }
}
