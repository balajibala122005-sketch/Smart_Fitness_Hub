package com.example.gymhub.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public class BodyMeasurementRequest {

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    @Min(value = 20, message = "Weight must be at least 20 kg")
    @Max(value = 500, message = "Weight cannot exceed 500 kg")
    private Double weight;

    @Positive(message = "Chest must be positive")
    @Min(value = 20, message = "Chest must be at least 20 cm")
    @Max(value = 300, message = "Chest cannot exceed 300 cm")
    private Double chest;

    @Positive(message = "Height must be positive")
    @Min(value = 50, message = "Height must be at least 50 cm")
    @Max(value = 300, message = "Height cannot exceed 300 cm")
    private Double height;

    @PastOrPresent(message = "Measurement date cannot be in the future")
    private LocalDate measurementDate;

    public BodyMeasurementRequest() {
    }

    public BodyMeasurementRequest(Double weight) {
        this.weight = weight;
    }

    public BodyMeasurementRequest(Double weight, Double chest, Double height, LocalDate measurementDate) {
        this.weight = weight;
        this.chest = chest;
        this.height = height;
        this.measurementDate = measurementDate;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getChest() {
        return chest;
    }

    public void setChest(Double chest) {
        this.chest = chest;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public LocalDate getMeasurementDate() {
        return measurementDate;
    }

    public void setMeasurementDate(LocalDate measurementDate) {
        this.measurementDate = measurementDate;
    }
}
