package com.example.gymhub.dto;

import com.example.gymhub.entity.BodyMeasurement;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BodyMeasurementResponse {

    private Long id;
    private Double weight;
    private Double chest;
    private Double height;
    private LocalDate measurementDate;
    private LocalDateTime createdAt;

    public BodyMeasurementResponse() {
    }

    public static BodyMeasurementResponse fromEntity(BodyMeasurement measurement) {
        if (measurement == null) {
            return null;
        }
        BodyMeasurementResponse res = new BodyMeasurementResponse();
        res.setId(measurement.getId());
        res.setWeight(measurement.getWeight());
        res.setChest(measurement.getChest());
        res.setHeight(measurement.getHeight());
        res.setMeasurementDate(measurement.getMeasurementDate());
        res.setCreatedAt(measurement.getCreatedAt());
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
