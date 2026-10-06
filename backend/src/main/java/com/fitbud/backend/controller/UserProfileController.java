package com.fitbud.backend.controller;

import com.fitbud.backend.dto.CreateUserProfileRequest;
import com.fitbud.backend.dto.UserProfileResponse;
import com.fitbud.backend.model.UserProfile;
import com.fitbud.backend.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;



@RestController
@RequestMapping("/api/profiles")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PostMapping
    public ResponseEntity<UserProfileResponse> createProfile(
            @Valid @RequestBody CreateUserProfileRequest request,
            Principal principal) {

        UserProfile userProfile = new UserProfile(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.heightCm(),
                request.weightKg()
        );

        UserProfile createdProfile =
                userProfileService.createProfile(
                        userProfile,
                        principal.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(createdProfile));
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(
            Principal principal) {

        return userProfileService
                .getProfileByAccountEmail(principal.getName())
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

   /* @GetMapping
    public ResponseEntity<List<UserProfileResponse>> getAllProfiles() {

        List<UserProfileResponse> profiles =
                userProfileService.getAllProfiles()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(profiles);
    }*/

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getProfileById(
            @PathVariable Long id,
            Principal principal) {

        return userProfileService
                .getProfileByIdAndAccountEmail(
                        id,
                        principal.getName()
                )
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private UserProfileResponse toResponse(UserProfile profile) {
        return new UserProfileResponse(
                profile.getId(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getEmail(),
                profile.getHeightCm(),
                profile.getWeightKg(),
                profile.getCreatedAt()
        );
    }
}