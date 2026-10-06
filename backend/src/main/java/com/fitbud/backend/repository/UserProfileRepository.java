package com.fitbud.backend.repository;

import com.fitbud.backend.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByEmail(String email);

    Optional<UserProfile> findByUserAccountEmail(String email);

    Optional<UserProfile> findByIdAndUserAccountEmail(
            Long id,
            String email
    );

    boolean existsByEmail(String email);
}