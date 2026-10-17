package com.example.gymhub.repository;

import com.example.gymhub.entity.User;
import com.example.gymhub.entity.UserWorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserWorkoutPlanRepository extends JpaRepository<UserWorkoutPlan, Long> {
    Optional<UserWorkoutPlan> findByUser(User user);
}
