package com.example.gymhub.repository;

import com.example.gymhub.entity.BodyMeasurement;
import com.example.gymhub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BodyMeasurementRepository extends JpaRepository<BodyMeasurement, Long> {
    List<BodyMeasurement> findByUserOrderByMeasurementDateAscCreatedAtAsc(User user);
    Optional<BodyMeasurement> findTopByUserOrderByMeasurementDateDescCreatedAtDesc(User user);
}
