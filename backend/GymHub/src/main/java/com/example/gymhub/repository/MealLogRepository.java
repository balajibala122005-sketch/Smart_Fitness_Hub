package com.example.gymhub.repository;

import com.example.gymhub.entity.MealLog;
import com.example.gymhub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MealLogRepository extends JpaRepository<MealLog, Long> {

    List<MealLog> findByUserOrderByMealDateDescCreatedAtDesc(User user);

    List<MealLog> findByUserAndMealDateOrderByCreatedAtDesc(User user, LocalDate mealDate);

    Optional<MealLog> findByIdAndUser(Long id, User user);

    long countByUser(User user);

    long countByUserAndMealDate(User user, LocalDate mealDate);
}
