package com.fitbud.backend.service;

import com.fitbud.backend.model.Food;
import com.fitbud.backend.model.FoodLog;
import com.fitbud.backend.model.MealType;
import com.fitbud.backend.model.UserProfile;
import com.fitbud.backend.repository.FoodLogRepository;
import com.fitbud.backend.repository.FoodRepository;
import com.fitbud.backend.repository.UserProfileRepository;
import com.fitbud.backend.exception.FoodNotFoundException;
import com.fitbud.backend.exception.UserProfileNotFoundException;
import org.springframework.stereotype.Service;
import com.fitbud.backend.exception.FoodLogNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FoodLogService {

    private final FoodLogRepository foodLogRepository;
    private final UserProfileRepository userProfileRepository;
    private final FoodRepository foodRepository;

    public FoodLogService(
            FoodLogRepository foodLogRepository,
            UserProfileRepository userProfileRepository,
            FoodRepository foodRepository) {

        this.foodLogRepository = foodLogRepository;
        this.userProfileRepository = userProfileRepository;
        this.foodRepository = foodRepository;
    }

    public FoodLog createFoodLog(
            Long userProfileId,
            Long foodId,
            MealType mealType,
            Double quantityGrams,
            LocalDateTime eatenAt) {

        UserProfile userProfile = userProfileRepository
                .findById(userProfileId)
                .orElseThrow(() -> new UserProfileNotFoundException(
                        "User profile not found with id: " + userProfileId
                ));

        Food food = foodRepository
                .findById(foodId)
                .orElseThrow(() -> new FoodNotFoundException(
                        "Food not found with id: " + foodId
                ));

        FoodLog foodLog = new FoodLog(
                userProfile,
                food,
                mealType,
                quantityGrams,
                eatenAt
        );

        return foodLogRepository.save(foodLog);
    }

    public List<FoodLog> getFoodLogsByUser(Long userProfileId) {

        ensureUserProfileExists(userProfileId);

        return foodLogRepository
                .findByUserProfileIdOrderByEatenAtDesc(userProfileId);
    }

    public List<FoodLog> getFoodLogsByUserAndDate(
            Long userProfileId,
            LocalDate date) {

        ensureUserProfileExists(userProfileId);

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime startOfNextDay = date.plusDays(1).atStartOfDay();

        return foodLogRepository
                .findByUserProfileIdAndEatenAtGreaterThanEqualAndEatenAtLessThanOrderByEatenAtAsc(
                        userProfileId,
                        startOfDay,
                        startOfNextDay
                );
    }

    public void deleteFoodLog(Long userProfileId, Long logId) {

        FoodLog foodLog = foodLogRepository
                .findByIdAndUserProfileId(logId, userProfileId)
                .orElseThrow(() -> new FoodLogNotFoundException(
                        "Food log not found with id: " + logId +
                                " for user profile: " + userProfileId
                ));

        foodLogRepository.delete(foodLog);
    }
    private void ensureUserProfileExists(Long userProfileId) {
        if (!userProfileRepository.existsById(userProfileId)) {
            throw new UserProfileNotFoundException(
                    "User profile not found with id: " + userProfileId
            );
        }
    }
}