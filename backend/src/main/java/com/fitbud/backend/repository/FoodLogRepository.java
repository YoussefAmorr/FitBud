package com.fitbud.backend.repository;

import com.fitbud.backend.model.FoodLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;

public interface FoodLogRepository extends JpaRepository<FoodLog, Long> {

    List<FoodLog> findByUserProfileIdOrderByEatenAtDesc(Long userProfileId);

    List<FoodLog> findByUserProfileIdAndEatenAtGreaterThanEqualAndEatenAtLessThanOrderByEatenAtAsc(
            Long userProfileId,
            LocalDateTime start,
            LocalDateTime end
    );
    Optional<FoodLog> findByIdAndUserProfileId(
            Long id,
            Long userProfileId
    );
}