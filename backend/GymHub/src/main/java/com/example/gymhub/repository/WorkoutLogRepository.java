package com.example.gymhub.repository;

import com.example.gymhub.entity.User;
import com.example.gymhub.entity.WorkoutLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, Long> {
    List<WorkoutLog> findByUserOrderByWorkoutDateDescCreatedAtDesc(User user);
    long countByUser(User user);
}
