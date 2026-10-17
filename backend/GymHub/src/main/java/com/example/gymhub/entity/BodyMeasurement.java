package com.example.gymhub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "body_measurements")
public class BodyMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be a positive number")
    @Column(nullable = false)
    private Double weight;

    @Positive(message = "Chest measurement must be a positive number")
    private Double chest;

    private Double height;

    @Column(nullable = false)
    private LocalDate measurementDate = LocalDate.now();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public BodyMeasurement() {
    }

    public BodyMeasurement(User user, Double weight, Double chest, Double height, LocalDate measurementDate) {
        this.user = user;
        this.weight = weight;
        this.chest = chest;
        this.height = height;
        this.measurementDate = measurementDate != null ? measurementDate : LocalDate.now();
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
