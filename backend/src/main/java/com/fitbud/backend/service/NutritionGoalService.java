package com.fitbud.backend.service;
import com.fitbud.backend.exception.NutritionGoalAlreadyExistsException;
import com.fitbud.backend.model.NutritionGoal;
import com.fitbud.backend.exception.UserProfileNotFoundException;
import com.fitbud.backend.model.UserProfile;
import com.fitbud.backend.repository.NutritionGoalRepository;
import com.fitbud.backend.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NutritionGoalService {

    private final NutritionGoalRepository nutritionGoalRepository;
    private final UserProfileRepository userProfileRepository;

    public NutritionGoalService(
            NutritionGoalRepository nutritionGoalRepository,
            UserProfileRepository userProfileRepository) {

        this.nutritionGoalRepository = nutritionGoalRepository;
        this.userProfileRepository = userProfileRepository;
    }

    public NutritionGoal createNutritionGoal(
            Long userProfileId,
            String authenticatedEmail,
            Integer calorieTarget,
            Integer proteinTargetGrams,
            Integer carbohydrateTargetGrams,
            Integer fatTargetGrams) {

        UserProfile userProfile = userProfileRepository
                .findByIdAndUserAccountEmail(
                        userProfileId,
                        authenticatedEmail
                )
                .orElseThrow(() ->
                        new UserProfileNotFoundException(
                                "User profile not found with id: " + userProfileId
                        )
                );

        if (nutritionGoalRepository.existsByUserProfileId(userProfileId)) {
            throw new NutritionGoalAlreadyExistsException(
                    "Nutrition goals already exist for this user profile."
            );
        }

        NutritionGoal nutritionGoal = new NutritionGoal(
                userProfile,
                calorieTarget,
                proteinTargetGrams,
                carbohydrateTargetGrams,
                fatTargetGrams
        );

        return nutritionGoalRepository.save(nutritionGoal);
    }

    public Optional<NutritionGoal> getNutritionGoalByUserProfileId(
            Long userProfileId,
            String authenticatedEmail) {

        if (userProfileRepository
                .findByIdAndUserAccountEmail(
                        userProfileId,
                        authenticatedEmail
                )
                .isEmpty()) {

            return Optional.empty();
        }

        return nutritionGoalRepository.findByUserProfileId(userProfileId);
    }
}