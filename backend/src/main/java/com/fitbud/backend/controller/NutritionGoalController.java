package com.fitbud.backend.controller;

import com.fitbud.backend.dto.CreateNutritionGoalRequest;
import com.fitbud.backend.dto.NutritionGoalResponse;
import com.fitbud.backend.model.NutritionGoal;
import com.fitbud.backend.service.NutritionGoalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
@RestController
@RequestMapping("/api/profiles/{profileId}/nutrition-goals")
public class NutritionGoalController {

    private final NutritionGoalService nutritionGoalService;

    public NutritionGoalController(
            NutritionGoalService nutritionGoalService) {
        this.nutritionGoalService = nutritionGoalService;
    }

    @PostMapping
    public ResponseEntity<NutritionGoalResponse> createNutritionGoal(
            @PathVariable Long profileId,
            @Valid @RequestBody CreateNutritionGoalRequest request,
            Principal principal) {

        NutritionGoal nutritionGoal =
                nutritionGoalService.createNutritionGoal(
                        profileId,
                        principal.getName(),
                        request.calorieTarget(),
                        request.proteinTargetGrams(),
                        request.carbohydrateTargetGrams(),
                        request.fatTargetGrams()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(nutritionGoal));
    }

    @GetMapping
    public ResponseEntity<NutritionGoalResponse> getNutritionGoal(
            @PathVariable Long profileId,
            Principal principal) {

        return nutritionGoalService
                .getNutritionGoalByUserProfileId(
                        profileId,
                        principal.getName()
                )
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private NutritionGoalResponse toResponse(
            NutritionGoal nutritionGoal) {

        return new NutritionGoalResponse(
                nutritionGoal.getId(),
                nutritionGoal.getUserProfile().getId(),
                nutritionGoal.getCalorieTarget(),
                nutritionGoal.getProteinTargetGrams(),
                nutritionGoal.getCarbohydrateTargetGrams(),
                nutritionGoal.getFatTargetGrams(),
                nutritionGoal.getCreatedAt()
        );
    }
}