package com.fitbud.backend.service;

import com.fitbud.backend.dto.DailyNutritionSummaryResponse;
import com.fitbud.backend.exception.NutritionGoalNotFoundException;
import com.fitbud.backend.exception.UserProfileNotFoundException;
import com.fitbud.backend.model.Food;
import com.fitbud.backend.model.FoodLog;
import com.fitbud.backend.model.NutritionGoal;
import com.fitbud.backend.repository.FoodLogRepository;
import com.fitbud.backend.repository.NutritionGoalRepository;
import com.fitbud.backend.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NutritionSummaryService {

    private final FoodLogRepository foodLogRepository;
    private final NutritionGoalRepository nutritionGoalRepository;
    private final UserProfileRepository userProfileRepository;

    public NutritionSummaryService(
            FoodLogRepository foodLogRepository,
            NutritionGoalRepository nutritionGoalRepository,
            UserProfileRepository userProfileRepository) {

        this.foodLogRepository = foodLogRepository;
        this.nutritionGoalRepository = nutritionGoalRepository;
        this.userProfileRepository = userProfileRepository;
    }

    public DailyNutritionSummaryResponse getDailySummary(
            Long userProfileId,
            String authenticatedEmail,
            LocalDate date) {

        if (userProfileRepository
                .findByIdAndUserAccountEmail(
                        userProfileId,
                        authenticatedEmail
                )
                .isEmpty()) {

            throw new UserProfileNotFoundException(
                    "User profile not found with id: " + userProfileId
            );
        }

        NutritionGoal nutritionGoal = nutritionGoalRepository
                .findByUserProfileId(userProfileId)
                .orElseThrow(() -> new NutritionGoalNotFoundException(
                        "Nutrition goal not found for user profile: " + userProfileId
                ));

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime startOfNextDay = date.plusDays(1).atStartOfDay();

        List<FoodLog> foodLogs = foodLogRepository
                .findByUserProfileIdAndEatenAtGreaterThanEqualAndEatenAtLessThanOrderByEatenAtAsc(
                        userProfileId,
                        startOfDay,
                        startOfNextDay
                );

        double caloriesConsumed = 0.0;
        double proteinConsumed = 0.0;
        double carbohydrateConsumed = 0.0;
        double fatConsumed = 0.0;

        for (FoodLog foodLog : foodLogs) {

            Food food = foodLog.getFood();

            double servingMultiplier =
                    foodLog.getQuantityGrams()
                            / food.getServingSizeGrams();

            caloriesConsumed +=
                    food.getCalories() * servingMultiplier;

            proteinConsumed +=
                    food.getProteinGrams() * servingMultiplier;

            carbohydrateConsumed +=
                    food.getCarbohydrateGrams() * servingMultiplier;

            fatConsumed +=
                    food.getFatGrams() * servingMultiplier;
        }

        return new DailyNutritionSummaryResponse(
                userProfileId,
                date,

                caloriesConsumed,
                nutritionGoal.getCalorieTarget(),
                nutritionGoal.getCalorieTarget() - caloriesConsumed,

                proteinConsumed,
                nutritionGoal.getProteinTargetGrams(),
                nutritionGoal.getProteinTargetGrams() - proteinConsumed,

                carbohydrateConsumed,
                nutritionGoal.getCarbohydrateTargetGrams(),
                nutritionGoal.getCarbohydrateTargetGrams()
                        - carbohydrateConsumed,

                fatConsumed,
                nutritionGoal.getFatTargetGrams(),
                nutritionGoal.getFatTargetGrams() - fatConsumed
        );
    }
}