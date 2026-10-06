package com.fitbud.backend.repository;

import com.fitbud.backend.model.NutritionGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NutritionGoalRepository extends JpaRepository<NutritionGoal, Long> {

    Optional<NutritionGoal> findByUserProfileId(Long userProfileId);

    boolean existsByUserProfileId(Long userProfileId);
}