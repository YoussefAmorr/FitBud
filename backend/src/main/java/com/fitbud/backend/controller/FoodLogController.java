package com.fitbud.backend.controller;

import com.fitbud.backend.dto.CreateFoodLogRequest;
import com.fitbud.backend.dto.FoodLogResponse;
import com.fitbud.backend.model.Food;
import com.fitbud.backend.model.FoodLog;
import com.fitbud.backend.service.FoodLogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/profiles/{profileId}/food-logs")
public class FoodLogController {

    private final FoodLogService foodLogService;

    public FoodLogController(FoodLogService foodLogService) {
        this.foodLogService = foodLogService;
    }

    @PostMapping
    public ResponseEntity<FoodLogResponse> createFoodLog(
            @PathVariable Long profileId,
            @Valid @RequestBody CreateFoodLogRequest request,
            Principal principal) {

        FoodLog foodLog = foodLogService.createFoodLog(
                profileId,
                principal.getName(),
                request.foodId(),
                request.mealType(),
                request.quantityGrams(),
                request.eatenAt()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(foodLog));
    }

    @GetMapping
    public ResponseEntity<List<FoodLogResponse>> getFoodLogs(
            @PathVariable Long profileId,
            @RequestParam(required = false) LocalDate date,
            Principal principal) {

        List<FoodLog> foodLogs;

        if (date != null) {
            foodLogs = foodLogService.getFoodLogsByUserAndDate(
                    profileId,
                    principal.getName(),
                    date
            );
        } else {
            foodLogs = foodLogService.getFoodLogsByUser(
                    profileId,
                    principal.getName()
            );
        }

        List<FoodLogResponse> response = foodLogs
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{logId}")
    public ResponseEntity<Void> deleteFoodLog(
            @PathVariable Long profileId,
            @PathVariable Long logId,
            Principal principal) {

        foodLogService.deleteFoodLog(
                profileId,
                principal.getName(),
                logId
        );

        return ResponseEntity.noContent().build();
    }

    private FoodLogResponse toResponse(FoodLog foodLog) {

        Food food = foodLog.getFood();

        double servingMultiplier =
                foodLog.getQuantityGrams() / food.getServingSizeGrams();

        double calories =
                food.getCalories() * servingMultiplier;

        double protein =
                food.getProteinGrams() * servingMultiplier;

        double carbohydrates =
                food.getCarbohydrateGrams() * servingMultiplier;

        double fat =
                food.getFatGrams() * servingMultiplier;

        return new FoodLogResponse(
                foodLog.getId(),
                foodLog.getUserProfile().getId(),
                food.getId(),
                food.getName(),
                food.getBrand(),
                foodLog.getMealType(),
                foodLog.getQuantityGrams(),
                calories,
                protein,
                carbohydrates,
                fat,
                foodLog.getEatenAt(),
                foodLog.getCreatedAt()
        );
    }
}