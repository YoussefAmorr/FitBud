package com.fitbud.backend.service;

import com.fitbud.backend.exception.NutritionGoalAlreadyExistsException;
import com.fitbud.backend.exception.UserProfileNotFoundException;
import com.fitbud.backend.model.NutritionGoal;
import com.fitbud.backend.model.UserProfile;
import com.fitbud.backend.repository.NutritionGoalRepository;
import com.fitbud.backend.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NutritionGoalServiceTest {

    @Mock
    private NutritionGoalRepository nutritionGoalRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    private NutritionGoalService nutritionGoalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        nutritionGoalService = new NutritionGoalService(
                nutritionGoalRepository,
                userProfileRepository
        );
    }

    @Test
    void shouldCreateNutritionGoalForOwnedProfile() {

        Long profileId = 1L;
        String authenticatedEmail = "test@fitbud.com";

        UserProfile userProfile = mock(UserProfile.class);

        when(userProfileRepository.findByIdAndUserAccountEmail(
                profileId,
                authenticatedEmail
        )).thenReturn(Optional.of(userProfile));

        when(nutritionGoalRepository.existsByUserProfileId(profileId))
                .thenReturn(false);

        when(nutritionGoalRepository.save(any(NutritionGoal.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        NutritionGoal result =
                nutritionGoalService.createNutritionGoal(
                        profileId,
                        authenticatedEmail,
                        2300,
                        180,
                        220,
                        70
                );

        assertNotNull(result);

        verify(userProfileRepository)
                .findByIdAndUserAccountEmail(
                        profileId,
                        authenticatedEmail
                );

        verify(nutritionGoalRepository)
                .existsByUserProfileId(profileId);

        verify(nutritionGoalRepository)
                .save(any(NutritionGoal.class));
    }

    @Test
    void shouldRejectNutritionGoalForProfileOwnedByAnotherAccount() {

        Long profileId = 1L;
        String authenticatedEmail = "otheruser@fitbud.com";

        when(userProfileRepository.findByIdAndUserAccountEmail(
                profileId,
                authenticatedEmail
        )).thenReturn(Optional.empty());

        assertThrows(
                UserProfileNotFoundException.class,
                () -> nutritionGoalService.createNutritionGoal(
                        profileId,
                        authenticatedEmail,
                        2300,
                        180,
                        220,
                        70
                )
        );

        verify(nutritionGoalRepository, never())
                .existsByUserProfileId(anyLong());

        verify(nutritionGoalRepository, never())
                .save(any(NutritionGoal.class));
    }

    @Test
    void shouldRejectDuplicateNutritionGoal() {

        Long profileId = 1L;
        String authenticatedEmail = "test@fitbud.com";

        UserProfile userProfile = mock(UserProfile.class);

        when(userProfileRepository.findByIdAndUserAccountEmail(
                profileId,
                authenticatedEmail
        )).thenReturn(Optional.of(userProfile));

        when(nutritionGoalRepository.existsByUserProfileId(profileId))
                .thenReturn(true);

        assertThrows(
                NutritionGoalAlreadyExistsException.class,
                () -> nutritionGoalService.createNutritionGoal(
                        profileId,
                        authenticatedEmail,
                        2300,
                        180,
                        220,
                        70
                )
        );

        verify(nutritionGoalRepository, never())
                .save(any(NutritionGoal.class));
    }

    @Test
    void shouldGetNutritionGoalForOwnedProfile() {

        Long profileId = 1L;
        String authenticatedEmail = "test@fitbud.com";

        UserProfile userProfile = mock(UserProfile.class);
        NutritionGoal nutritionGoal = mock(NutritionGoal.class);

        when(userProfileRepository.findByIdAndUserAccountEmail(
                profileId,
                authenticatedEmail
        )).thenReturn(Optional.of(userProfile));

        when(nutritionGoalRepository.findByUserProfileId(profileId))
                .thenReturn(Optional.of(nutritionGoal));

        Optional<NutritionGoal> result =
                nutritionGoalService.getNutritionGoalByUserProfileId(
                        profileId,
                        authenticatedEmail
                );

        assertTrue(result.isPresent());
        assertSame(nutritionGoal, result.get());

        verify(nutritionGoalRepository)
                .findByUserProfileId(profileId);
    }

    @Test
    void shouldReturnEmptyWhenReadingProfileOwnedByAnotherAccount() {

        Long profileId = 1L;
        String authenticatedEmail = "otheruser@fitbud.com";

        when(userProfileRepository.findByIdAndUserAccountEmail(
                profileId,
                authenticatedEmail
        )).thenReturn(Optional.empty());

        Optional<NutritionGoal> result =
                nutritionGoalService.getNutritionGoalByUserProfileId(
                        profileId,
                        authenticatedEmail
                );

        assertTrue(result.isEmpty());

        verify(nutritionGoalRepository, never())
                .findByUserProfileId(anyLong());
    }

    @Test
    void shouldReturnEmptyWhenOwnedProfileHasNoNutritionGoal() {

        Long profileId = 1L;
        String authenticatedEmail = "test@fitbud.com";

        UserProfile userProfile = mock(UserProfile.class);

        when(userProfileRepository.findByIdAndUserAccountEmail(
                profileId,
                authenticatedEmail
        )).thenReturn(Optional.of(userProfile));

        when(nutritionGoalRepository.findByUserProfileId(profileId))
                .thenReturn(Optional.empty());

        Optional<NutritionGoal> result =
                nutritionGoalService.getNutritionGoalByUserProfileId(
                        profileId,
                        authenticatedEmail
                );

        assertTrue(result.isEmpty());

        verify(nutritionGoalRepository)
                .findByUserProfileId(profileId);
    }
}