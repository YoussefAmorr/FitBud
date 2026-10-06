package com.fitbud.backend.service;

import com.fitbud.backend.exception.DuplicateEmailException;
import com.fitbud.backend.model.UserProfile;
import com.fitbud.backend.repository.UserAccountRepository;
import com.fitbud.backend.repository.UserProfileRepository;
import org.springframework.stereotype.Service;
import com.fitbud.backend.exception.UserAccountNotFoundException;
import com.fitbud.backend.model.UserAccount;
import com.fitbud.backend.exception.UserProfileAlreadyExistsException;
import java.util.List;
import java.util.Optional;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserAccountRepository userAccountRepository;

    public UserProfileService(
            UserProfileRepository userProfileRepository,
            UserAccountRepository userAccountRepository) {

        this.userProfileRepository = userProfileRepository;
        this.userAccountRepository = userAccountRepository;
    }

    public UserProfile createProfile(
            UserProfile userProfile,
            String authenticatedEmail) {
        userProfile.setEmail(authenticatedEmail);

        if (userProfileRepository.existsByEmail(userProfile.getEmail())) {
            throw new DuplicateEmailException(
                    "A user profile with this email already exists."
            );
        }
        if (userProfileRepository
                .findByUserAccountEmail(authenticatedEmail)
                .isPresent()) {

            throw new UserProfileAlreadyExistsException(
                    "A profile already exists for this account."
            );
        }

        UserAccount userAccount = userAccountRepository
                .findByEmail(authenticatedEmail)
                .orElseThrow(() -> new UserAccountNotFoundException(
                        "Authenticated user account was not found."
                ));

        userProfile.setUserAccount(userAccount);

        return userProfileRepository.save(userProfile);
    }

    public List<UserProfile> getAllProfiles() {
        return userProfileRepository.findAll();
    }

    public Optional<UserProfile> getProfileById(Long id) {
        return userProfileRepository.findById(id);
    }

    public Optional<UserProfile> getProfileByEmail(String email) {
        return userProfileRepository.findByEmail(email);
    }

    public Optional<UserProfile> getProfileByAccountEmail(String email) {
        return userProfileRepository.findByUserAccountEmail(email);
    }

    public Optional<UserProfile> getProfileByIdAndAccountEmail(
            Long id,
            String authenticatedEmail) {

        return userProfileRepository.findByIdAndUserAccountEmail(
                id,
                authenticatedEmail
        );
    }
}