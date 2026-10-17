package com.example.gymhub.dto;

import com.example.gymhub.entity.User;

public class DashboardSummaryDto {

    private Long id;
    private String name;
    private String email;
    private String role;
    private Integer age;
    private Double height;
    private Double weight;
    private String fitnessGoal;

    // Supported statistics with clearly defined defaults
    private Double bmi;
    private String bmiStatus;
    private Integer targetCalories;
    private Integer totalWorkouts;
    private String activePlan;
    private Integer waterIntakeToday;
    private Integer todayCalories;

    public DashboardSummaryDto() {
    }

    public static DashboardSummaryDto fromUser(User user) {
        if (user == null) {
            return null;
        }

        DashboardSummaryDto dto = new DashboardSummaryDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole() != null ? user.getRole().name() : "USER");
        dto.setAge(user.getAge());
        dto.setHeight(user.getHeight());
        dto.setWeight(user.getWeight());
        dto.setFitnessGoal(user.getGoal());

        // Calculate BMI and Daily Energy Target if height and weight exist
        if (user.getHeight() != null && user.getHeight() > 0 && user.getWeight() != null && user.getWeight() > 0) {
            double heightM = user.getHeight() / 100.0;
            double rawBmi = user.getWeight() / (heightM * heightM);
            double roundedBmi = Math.round(rawBmi * 10.0) / 10.0;
            dto.setBmi(roundedBmi);

            if (roundedBmi < 18.5) {
                dto.setBmiStatus("Under Threshold");
            } else if (roundedBmi < 24.9) {
                dto.setBmiStatus("Optimal Condition");
            } else if (roundedBmi < 29.9) {
                dto.setBmiStatus("Above Baseline");
            } else {
                dto.setBmiStatus("Critical Mass");
            }

            dto.setTargetCalories((int) Math.round(user.getWeight() * 30));
        } else {
            dto.setBmi(null);
            dto.setBmiStatus("Pending Measurement");
            dto.setTargetCalories(null);
        }

        // Defined defaults for modules implemented in later phases
        dto.setTotalWorkouts(0);
        dto.setActivePlan("None Selected");
        dto.setWaterIntakeToday(0);

        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getFitnessGoal() {
        return fitnessGoal;
    }

    public void setFitnessGoal(String fitnessGoal) {
        this.fitnessGoal = fitnessGoal;
    }

    public Double getBmi() {
        return bmi;
    }

    public void setBmi(Double bmi) {
        this.bmi = bmi;
    }

    public String getBmiStatus() {
        return bmiStatus;
    }

    public void setBmiStatus(String bmiStatus) {
        this.bmiStatus = bmiStatus;
    }

    public Integer getTargetCalories() {
        return targetCalories;
    }

    public void setTargetCalories(Integer targetCalories) {
        this.targetCalories = targetCalories;
    }

    public Integer getTotalWorkouts() {
        return totalWorkouts;
    }

    public void setTotalWorkouts(Integer totalWorkouts) {
        this.totalWorkouts = totalWorkouts;
    }

    public String getActivePlan() {
        return activePlan;
    }

    public void setActivePlan(String activePlan) {
        this.activePlan = activePlan;
    }

    public Integer getWaterIntakeToday() {
        return waterIntakeToday;
    }

    public void setWaterIntakeToday(Integer waterIntakeToday) {
        this.waterIntakeToday = waterIntakeToday;
    }

    public Integer getTodayCalories() {
        return todayCalories;
    }

    public void setTodayCalories(Integer todayCalories) {
        this.todayCalories = todayCalories;
    }
}
