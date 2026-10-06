package com.fitbud.backend.service;

import com.fitbud.backend.exception.FoodLogNotFoundException;
import com.fitbud.backend.exception.FoodNotFoundException;
import com.fitbud.backend.exception.UserProfileNotFoundException;
import com.fitbud.backend.model.Food;
import com.fitbud.backend.model.FoodLog;
import com.fitbud.backend.model.MealType;
import com.fitbud.backend.model.UserProfile;
import com.fitbud.backend.repository.FoodLogRepository;
import com.fitbud.backend.repository.FoodRepository;
import com.fitbud.backend.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

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
            String authenticatedEmail,
            Long foodId,
            MealType mealType,
            Double quantityGrams,
            LocalDateTime eatenAt) {

        UserProfile userProfile = getOwnedUserProfile(
                userProfileId,
                authenticatedEmail
        );

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

    public List<FoodLog> getFoodLogsByUser(
            Long userProfileId,
            String authenticatedEmail) {

        ensureUserOwnsProfile(
                userProfileId,
                authenticatedEmail
        );

        return foodLogRepository
                .findByUserProfileIdOrderByEatenAtDesc(userProfileId);
    }

    public List<FoodLog> getFoodLogsByUserAndDate(
            Long userProfileId,
            String authenticatedEmail,
            LocalDate date) {

        ensureUserOwnsProfile(
                userProfileId,
                authenticatedEmail
        );

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime startOfNextDay = date.plusDays(1).atStartOfDay();

        return foodLogRepository
                .findByUserProfileIdAndEatenAtGreaterThanEqualAndEatenAtLessThanOrderByEatenAtAsc(
                        userProfileId,
                        startOfDay,
                        startOfNextDay
                );
    }

    public void deleteFoodLog(
            Long userProfileId,
            String authenticatedEmail,
            Long logId) {

        ensureUserOwnsProfile(
                userProfileId,
                authenticatedEmail
        );

        FoodLog foodLog = foodLogRepository
                .findByIdAndUserProfileId(logId, userProfileId)
                .orElseThrow(() -> new FoodLogNotFoundException(
                        "Food log not found with id: " + logId +
                                " for user profile: " + userProfileId
                ));

        foodLogRepository.delete(foodLog);
    }

    private UserProfile getOwnedUserProfile(
            Long userProfileId,
            String authenticatedEmail) {

        return userProfileRepository
                .findByIdAndUserAccountEmail(
                        userProfileId,
                        authenticatedEmail
                )
                .orElseThrow(() -> new UserProfileNotFoundException(
                        "User profile not found with id: " + userProfileId
                ));
    }

    private void ensureUserOwnsProfile(
            Long userProfileId,
            String authenticatedEmail) {

        getOwnedUserProfile(
                userProfileId,
                authenticatedEmail
        );
    }
}