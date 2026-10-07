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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FoodLogServiceTest {

    @Mock
    private FoodLogRepository foodLogRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private FoodRepository foodRepository;

    private FoodLogService foodLogService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        foodLogService = new FoodLogService(
                foodLogRepository,
                userProfileRepository,
                foodRepository
        );
    }

    @Test
    void shouldCreateFoodLogForOwnedProfile() {

        Long profileId = 1L;
        Long foodId = 10L;
        String authenticatedEmail = "test@fitbud.com";
        LocalDateTime eatenAt =
                LocalDateTime.of(2026, 10, 7, 12, 30);

        UserProfile userProfile = mock(UserProfile.class);
        Food food = mock(Food.class);

        when(userProfileRepository.findByIdAndUserAccountEmail(
                profileId,
                authenticatedEmail
        )).thenReturn(Optional.of(userProfile));

        when(foodRepository.findById(foodId))
                .thenReturn(Optional.of(food));

        when(foodLogRepository.save(any(FoodLog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        FoodLog result = foodLogService.createFoodLog(
                profileId,
                authenticatedEmail,
                foodId,
                MealType.LUNCH,
                150.0,
                eatenAt
        );

        assertNotNull(result);

        verify(userProfileRepository)
                .findByIdAndUserAccountEmail(
                        profileId,
                        authenticatedEmail
                );

        verify(foodRepository).findById(foodId);

        verify(foodLogRepository)
                .save(any(FoodLog.class));
    }

    @Test
    void shouldRejectFoodLogForProfileOwnedByAnotherAccount() {

        Long profileId = 1L;
        Long foodId = 10L;
        String authenticatedEmail = "otheruser@fitbud.com";

        when(userProfileRepository.findByIdAndUserAccountEmail(
                profileId,
                authenticatedEmail
        )).thenReturn(Optional.empty());

        assertThrows(
                UserProfileNotFoundException.class,
                () -> foodLogService.createFoodLog(
                        profileId,
                        authenticatedEmail,
                        foodId,
                        MealType.LUNCH,
                        150.0,
                        LocalDateTime.now()
                )
        );

        verify(foodRepository, never()).findById(anyLong());
        verify(foodLogRepository, never())
                .save(any(FoodLog.class));
    }

    @Test
    void shouldRejectFoodLogWhenFoodDoesNotExist() {

        Long profileId = 1L;
        Long foodId = 999L;
        String authenticatedEmail = "test@fitbud.com";

        UserProfile userProfile = mock(UserProfile.class);

        when(userProfileRepository.findByIdAndUserAccountEmail(
                profileId,
                authenticatedEmail
        )).thenReturn(Optional.of(userProfile));

        when(foodRepository.findById(foodId))
                .thenReturn(Optional.empty());

        assertThrows(
                FoodNotFoundException.class,
                () -> foodLogService.createFoodLog(
                        profileId,
                        authenticatedEmail,
                        foodId,
                        MealType.LUNCH,
                        150.0,
                        LocalDateTime.now()
                )
        );

        verify(foodLogRepository, never())
                .save(any(FoodLog.class));
    }

    @Test
    void shouldGetFoodLogsForOwnedProfile() {

        Long profileId = 1L;
        String authenticatedEmail = "test@fitbud.com";

        UserProfile userProfile = mock(UserProfile.class);
        FoodLog logOne = mock(FoodLog.class);
        FoodLog logTwo = mock(FoodLog.class);

        List<FoodLog> logs = List.of(
                logOne,
                logTwo
        );

        when(userProfileRepository.findByIdAndUserAccountEmail(
                profileId,
                authenticatedEmail
        )).thenReturn(Optional.of(userProfile));

        when(foodLogRepository
                .findByUserProfileIdOrderByEatenAtDesc(profileId))
                .thenReturn(logs);

        List<FoodLog> result =
                foodLogService.getFoodLogsByUser(
                        profileId,
                        authenticatedEmail
                );

        assertEquals(2, result.size());
        assertSame(logs, result);

        verify(foodLogRepository)
                .findByUserProfileIdOrderByEatenAtDesc(profileId);
    }

    @Test
    void shouldRejectGettingFoodLogsForAnotherUsersProfile() {

        Long profileId = 1L;
        String authenticatedEmail = "otheruser@fitbud.com";

        when(userProfileRepository.findByIdAndUserAccountEmail(
                profileId,
                authenticatedEmail
        )).thenReturn(Optional.empty());

        assertThrows(
                UserProfileNotFoundException.class,
                () -> foodLogService.getFoodLogsByUser(
                        profileId,
                        authenticatedEmail
                )
        );

        verify(foodLogRepository, never())
                .findByUserProfileIdOrderByEatenAtDesc(anyLong());
    }

    @Test
    void shouldGetFoodLogsForSpecificDate() {

        Long profileId = 1L;
        String authenticatedEmail = "test@fitbud.com";
        LocalDate date = LocalDate.of(2026, 10, 7);

        UserProfile userProfile = mock(UserProfile.class);
        FoodLog foodLog = mock(FoodLog.class);

        when(userProfileRepository.findByIdAndUserAccountEmail(
                profileId,
                authenticatedEmail
        )).thenReturn(Optional.of(userProfile));

        when(foodLogRepository
                .findByUserProfileIdAndEatenAtGreaterThanEqualAndEatenAtLessThanOrderByEatenAtAsc(
                        profileId,
                        date.atStartOfDay(),
                        date.plusDays(1).atStartOfDay()
                ))
                .thenReturn(List.of(foodLog));

        List<FoodLog> result =
                foodLogService.getFoodLogsByUserAndDate(
                        profileId,
                        authenticatedEmail,
                        date
                );

        assertEquals(1, result.size());
        assertSame(foodLog, result.get(0));

        verify(foodLogRepository)
                .findByUserProfileIdAndEatenAtGreaterThanEqualAndEatenAtLessThanOrderByEatenAtAsc(
                        profileId,
                        LocalDateTime.of(2026, 10, 7, 0, 0),
                        LocalDateTime.of(2026, 10, 8, 0, 0)
                );
    }

    @Test
    void shouldRejectDateSearchForAnotherUsersProfile() {

        Long profileId = 1L;
        String authenticatedEmail = "otheruser@fitbud.com";
        LocalDate date = LocalDate.of(2026, 10, 7);

        when(userProfileRepository.findByIdAndUserAccountEmail(
                profileId,
                authenticatedEmail
        )).thenReturn(Optional.empty());

        assertThrows(
                UserProfileNotFoundException.class,
                () -> foodLogService.getFoodLogsByUserAndDate(
                        profileId,
                        authenticatedEmail,
                        date
                )
        );

        verify(foodLogRepository, never())
                .findByUserProfileIdAndEatenAtGreaterThanEqualAndEatenAtLessThanOrderByEatenAtAsc(
                        anyLong(),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class)
                );
    }

    @Test
    void shouldDeleteFoodLogForOwnedProfile() {

        Long profileId = 1L;
        Long logId = 50L;
        String authenticatedEmail = "test@fitbud.com";

        UserProfile userProfile = mock(UserProfile.class);
        FoodLog foodLog = mock(FoodLog.class);

        when(userProfileRepository.findByIdAndUserAccountEmail(
                profileId,
                authenticatedEmail
        )).thenReturn(Optional.of(userProfile));

        when(foodLogRepository.findByIdAndUserProfileId(
                logId,
                profileId
        )).thenReturn(Optional.of(foodLog));

        foodLogService.deleteFoodLog(
                profileId,
                authenticatedEmail,
                logId
        );

        verify(foodLogRepository)
                .findByIdAndUserProfileId(
                        logId,
                        profileId
                );

        verify(foodLogRepository).delete(foodLog);
    }

    @Test
    void shouldRejectDeletingFoodLogFromAnotherUsersProfile() {

        Long profileId = 1L;
        Long logId = 50L;
        String authenticatedEmail = "otheruser@fitbud.com";

        when(userProfileRepository.findByIdAndUserAccountEmail(
                profileId,
                authenticatedEmail
        )).thenReturn(Optional.empty());

        assertThrows(
                UserProfileNotFoundException.class,
                () -> foodLogService.deleteFoodLog(
                        profileId,
                        authenticatedEmail,
                        logId
                )
        );

        verify(foodLogRepository, never())
                .findByIdAndUserProfileId(
                        anyLong(),
                        anyLong()
                );

        verify(foodLogRepository, never())
                .delete(any(FoodLog.class));
    }

    @Test
    void shouldRejectDeletingFoodLogThatDoesNotExist() {

        Long profileId = 1L;
        Long logId = 999L;
        String authenticatedEmail = "test@fitbud.com";

        UserProfile userProfile = mock(UserProfile.class);

        when(userProfileRepository.findByIdAndUserAccountEmail(
                profileId,
                authenticatedEmail
        )).thenReturn(Optional.of(userProfile));

        when(foodLogRepository.findByIdAndUserProfileId(
                logId,
                profileId
        )).thenReturn(Optional.empty());

        assertThrows(
                FoodLogNotFoundException.class,
                () -> foodLogService.deleteFoodLog(
                        profileId,
                        authenticatedEmail,
                        logId
                )
        );

        verify(foodLogRepository, never())
                .delete(any(FoodLog.class));
    }
}