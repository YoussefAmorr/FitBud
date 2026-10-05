package com.fitbud.backend.service;

import com.fitbud.backend.model.UserProfile;
import com.fitbud.backend.repository.UserProfileRepository;
import org.springframework.stereotype.Service;
import com.fitbud.backend.exception.DuplicateEmailException;

import java.util.List;
import java.util.Optional;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public UserProfile createProfile(UserProfile userProfile) {
        if (userProfileRepository.existsByEmail(userProfile.getEmail())) {
            throw new DuplicateEmailException(
                    "A user profile with this email already exists."
            );
        }

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
}