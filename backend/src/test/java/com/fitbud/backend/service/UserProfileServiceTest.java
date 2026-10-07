package com.fitbud.backend.service;

import com.fitbud.backend.exception.DuplicateEmailException;
import com.fitbud.backend.exception.UserAccountNotFoundException;
import com.fitbud.backend.exception.UserProfileAlreadyExistsException;
import com.fitbud.backend.model.UserAccount;
import com.fitbud.backend.model.UserProfile;
import com.fitbud.backend.repository.UserAccountRepository;
import com.fitbud.backend.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserProfileServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    private UserProfileService userProfileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userProfileService = new UserProfileService(
                userProfileRepository,
                userAccountRepository
        );
    }

    @Test
    void shouldCreateProfileForAuthenticatedUser() {

        String authenticatedEmail = "test@fitbud.com";

        UserProfile userProfile = mock(UserProfile.class);

        UserAccount userAccount = new UserAccount(
                authenticatedEmail,
                "hashed-password"
        );

        when(userProfile.getEmail())
                .thenReturn(authenticatedEmail);

        when(userProfileRepository.existsByEmail(authenticatedEmail))
                .thenReturn(false);

        when(userProfileRepository.findByUserAccountEmail(authenticatedEmail))
                .thenReturn(Optional.empty());

        when(userAccountRepository.findByEmail(authenticatedEmail))
                .thenReturn(Optional.of(userAccount));

        when(userProfileRepository.save(any(UserProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserProfile result = userProfileService.createProfile(
                userProfile,
                authenticatedEmail
        );

        assertSame(userProfile, result);

        verify(userProfile).setEmail(authenticatedEmail);
        verify(userProfile).setUserAccount(userAccount);
        verify(userProfileRepository).save(userProfile);
    }

    @Test
    void shouldOverrideProfileEmailWithAuthenticatedEmail() {

        String authenticatedEmail = "realuser@fitbud.com";

        UserProfile userProfile = mock(UserProfile.class);

        UserAccount userAccount = new UserAccount(
                authenticatedEmail,
                "hashed-password"
        );

        when(userProfile.getEmail())
                .thenReturn(authenticatedEmail);

        when(userProfileRepository.existsByEmail(authenticatedEmail))
                .thenReturn(false);

        when(userProfileRepository.findByUserAccountEmail(authenticatedEmail))
                .thenReturn(Optional.empty());

        when(userAccountRepository.findByEmail(authenticatedEmail))
                .thenReturn(Optional.of(userAccount));

        when(userProfileRepository.save(userProfile))
                .thenReturn(userProfile);

        userProfileService.createProfile(
                userProfile,
                authenticatedEmail
        );

        verify(userProfile).setEmail(authenticatedEmail);
        verify(userProfile).setUserAccount(userAccount);
        verify(userProfileRepository).save(userProfile);
    }

    @Test
    void shouldRejectProfileWhenEmailAlreadyExists() {

        String authenticatedEmail = "existing@fitbud.com";

        UserProfile userProfile = mock(UserProfile.class);

        when(userProfile.getEmail())
                .thenReturn(authenticatedEmail);

        when(userProfileRepository.existsByEmail(authenticatedEmail))
                .thenReturn(true);

        assertThrows(
                DuplicateEmailException.class,
                () -> userProfileService.createProfile(
                        userProfile,
                        authenticatedEmail
                )
        );

        verify(userProfile).setEmail(authenticatedEmail);
        verify(userProfileRepository).existsByEmail(authenticatedEmail);

        verify(userProfileRepository, never())
                .save(any(UserProfile.class));

        verify(userAccountRepository, never())
                .findByEmail(anyString());
    }

    @Test
    void shouldRejectProfileWhenAccountAlreadyHasProfile() {

        String authenticatedEmail = "test@fitbud.com";

        UserProfile newProfile = mock(UserProfile.class);
        UserProfile existingProfile = mock(UserProfile.class);

        when(newProfile.getEmail())
                .thenReturn(authenticatedEmail);

        when(userProfileRepository.existsByEmail(authenticatedEmail))
                .thenReturn(false);

        when(userProfileRepository.findByUserAccountEmail(authenticatedEmail))
                .thenReturn(Optional.of(existingProfile));

        assertThrows(
                UserProfileAlreadyExistsException.class,
                () -> userProfileService.createProfile(
                        newProfile,
                        authenticatedEmail
                )
        );

        verify(userProfileRepository, never())
                .save(any(UserProfile.class));

        verify(userAccountRepository, never())
                .findByEmail(anyString());
    }

    @Test
    void shouldRejectProfileWhenAuthenticatedAccountDoesNotExist() {

        String authenticatedEmail = "missing@fitbud.com";

        UserProfile userProfile = mock(UserProfile.class);

        when(userProfile.getEmail())
                .thenReturn(authenticatedEmail);

        when(userProfileRepository.existsByEmail(authenticatedEmail))
                .thenReturn(false);

        when(userProfileRepository.findByUserAccountEmail(authenticatedEmail))
                .thenReturn(Optional.empty());

        when(userAccountRepository.findByEmail(authenticatedEmail))
                .thenReturn(Optional.empty());

        assertThrows(
                UserAccountNotFoundException.class,
                () -> userProfileService.createProfile(
                        userProfile,
                        authenticatedEmail
                )
        );

        verify(userProfileRepository, never())
                .save(any(UserProfile.class));
    }

    @Test
    void shouldGetAllProfiles() {

        UserProfile profileOne = mock(UserProfile.class);
        UserProfile profileTwo = mock(UserProfile.class);

        List<UserProfile> profiles = List.of(
                profileOne,
                profileTwo
        );

        when(userProfileRepository.findAll())
                .thenReturn(profiles);

        List<UserProfile> result =
                userProfileService.getAllProfiles();

        assertEquals(2, result.size());
        assertSame(profiles, result);

        verify(userProfileRepository).findAll();
    }

    @Test
    void shouldGetProfileById() {

        Long profileId = 1L;

        UserProfile userProfile = mock(UserProfile.class);

        when(userProfileRepository.findById(profileId))
                .thenReturn(Optional.of(userProfile));

        Optional<UserProfile> result =
                userProfileService.getProfileById(profileId);

        assertTrue(result.isPresent());
        assertSame(userProfile, result.get());

        verify(userProfileRepository).findById(profileId);
    }

    @Test
    void shouldGetProfileByEmail() {

        String email = "test@fitbud.com";

        UserProfile userProfile = mock(UserProfile.class);

        when(userProfileRepository.findByEmail(email))
                .thenReturn(Optional.of(userProfile));

        Optional<UserProfile> result =
                userProfileService.getProfileByEmail(email);

        assertTrue(result.isPresent());
        assertSame(userProfile, result.get());

        verify(userProfileRepository).findByEmail(email);
    }

    @Test
    void shouldGetProfileByAuthenticatedAccountEmail() {

        String authenticatedEmail = "test@fitbud.com";

        UserProfile userProfile = mock(UserProfile.class);

        when(userProfileRepository.findByUserAccountEmail(authenticatedEmail))
                .thenReturn(Optional.of(userProfile));

        Optional<UserProfile> result =
                userProfileService.getProfileByAccountEmail(
                        authenticatedEmail
                );

        assertTrue(result.isPresent());
        assertSame(userProfile, result.get());

        verify(userProfileRepository)
                .findByUserAccountEmail(authenticatedEmail);
    }

    @Test
    void shouldGetProfileOnlyWhenOwnedByAuthenticatedAccount() {

        Long profileId = 1L;
        String authenticatedEmail = "owner@fitbud.com";

        UserProfile userProfile = mock(UserProfile.class);

        when(userProfileRepository.findByIdAndUserAccountEmail(
                profileId,
                authenticatedEmail
        )).thenReturn(Optional.of(userProfile));

        Optional<UserProfile> result =
                userProfileService.getProfileByIdAndAccountEmail(
                        profileId,
                        authenticatedEmail
                );

        assertTrue(result.isPresent());
        assertSame(userProfile, result.get());

        verify(userProfileRepository)
                .findByIdAndUserAccountEmail(
                        profileId,
                        authenticatedEmail
                );
    }

    @Test
    void shouldReturnEmptyWhenProfileIsNotOwnedByAuthenticatedAccount() {

        Long profileId = 1L;
        String authenticatedEmail = "otheruser@fitbud.com";

        when(userProfileRepository.findByIdAndUserAccountEmail(
                profileId,
                authenticatedEmail
        )).thenReturn(Optional.empty());

        Optional<UserProfile> result =
                userProfileService.getProfileByIdAndAccountEmail(
                        profileId,
                        authenticatedEmail
                );

        assertTrue(result.isEmpty());

        verify(userProfileRepository)
                .findByIdAndUserAccountEmail(
                        profileId,
                        authenticatedEmail
                );
    }
}