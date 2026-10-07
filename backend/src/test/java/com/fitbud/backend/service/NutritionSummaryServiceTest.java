package com.fitbud.backend.service;

import com.fitbud.backend.dto.DailyNutritionSummaryResponse;
import com.fitbud.backend.model.Food;
import com.fitbud.backend.model.FoodLog;
import com.fitbud.backend.model.MealType;
import com.fitbud.backend.model.NutritionGoal;
import com.fitbud.backend.model.UserProfile;
import com.fitbud.backend.repository.FoodLogRepository;
import com.fitbud.backend.repository.NutritionGoalRepository;
import com.fitbud.backend.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.fitbud.backend.exception.UserProfileNotFoundException;
import com.fitbud.backend.exception.NutritionGoalNotFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class NutritionSummaryServiceTest {

    @Mock
    private FoodLogRepository foodLogRepository;

    @Mock
    private NutritionGoalRepository nutritionGoalRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    private NutritionSummaryService nutritionSummaryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        nutritionSummaryService = new NutritionSummaryService(
                foodLogRepository,
                nutritionGoalRepository,
                userProfileRepository
        );
    }

    @Test
    void shouldCalculateDailyNutritionSummary() {

        Long profileId = 1L;
        String authenticatedEmail = "test@fitbud.com";
        LocalDate date = LocalDate.of(2026, 10, 6);

        UserProfile userProfile = org.mockito.Mockito.mock(UserProfile.class);

        NutritionGoal nutritionGoal = new NutritionGoal(
                userProfile,
                2300,
                180,
                220,
                70
        );

        Food chicken = new Food(
                "Chicken Breast",
                null,
                100.0,
                165.0,
                31.0,
                0.0,
                3.6
        );

        FoodLog foodLog = new FoodLog(
                userProfile,
                chicken,
                MealType.LUNCH,
                150.0,
                LocalDateTime.of(2026, 10, 6, 12, 30)
        );

        when(userProfileRepository.findByIdAndUserAccountEmail(
                profileId,
                authenticatedEmail
        )).thenReturn(Optional.of(userProfile));

        when(nutritionGoalRepository.findByUserProfileId(profileId))
                .thenReturn(Optional.of(nutritionGoal));

        when(foodLogRepository
                .findByUserProfileIdAndEatenAtGreaterThanEqualAndEatenAtLessThanOrderByEatenAtAsc(
                        eq(profileId),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class)
                ))
                .thenReturn(List.of(foodLog));

        DailyNutritionSummaryResponse summary =
                nutritionSummaryService.getDailySummary(
                        profileId,
                        authenticatedEmail,
                        date
                );

        assertEquals(247.5, summary.caloriesConsumed(), 0.001);
        assertEquals(46.5, summary.proteinConsumedGrams(), 0.001);
        assertEquals(0.0, summary.carbohydrateConsumedGrams(), 0.001);
        assertEquals(5.4, summary.fatConsumedGrams(), 0.001);
    }

    @Test
    void shouldRejectNutritionSummaryForProfileOwnedByAnotherAccount() {

        Long profileId = 1L;
        String authenticatedEmail = "otheruser@fitbud.com";
        LocalDate date = LocalDate.of(2026, 10, 6);

        when(userProfileRepository.findByIdAndUserAccountEmail(
                profileId,
                authenticatedEmail
        )).thenReturn(Optional.empty());

        assertThrows(
                UserProfileNotFoundException.class,
                () -> nutritionSummaryService.getDailySummary(
                        profileId,
                        authenticatedEmail,
                        date
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenNutritionGoalDoesNotExist() {

        Long profileId = 1L;
        String authenticatedEmail = "test@fitbud.com";
        LocalDate date = LocalDate.of(2026, 10, 6);

        UserProfile userProfile = org.mockito.Mockito.mock(UserProfile.class);

        when(userProfileRepository.findByIdAndUserAccountEmail(
                profileId,
                authenticatedEmail
        )).thenReturn(Optional.of(userProfile));

        when(nutritionGoalRepository.findByUserProfileId(profileId))
                .thenReturn(Optional.empty());

        assertThrows(
                NutritionGoalNotFoundException.class,
                () -> nutritionSummaryService.getDailySummary(
                        profileId,
                        authenticatedEmail,
                        date
                )
        );
    }
}