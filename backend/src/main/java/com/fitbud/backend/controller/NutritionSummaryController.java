package com.fitbud.backend.controller;

import com.fitbud.backend.dto.DailyNutritionSummaryResponse;
import com.fitbud.backend.service.NutritionSummaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/profiles/{profileId}/nutrition-summary")
public class NutritionSummaryController {

    private final NutritionSummaryService nutritionSummaryService;

    public NutritionSummaryController(
            NutritionSummaryService nutritionSummaryService) {
        this.nutritionSummaryService = nutritionSummaryService;
    }

    @GetMapping
    public ResponseEntity<DailyNutritionSummaryResponse> getDailySummary(
            @PathVariable Long profileId,
            @RequestParam LocalDate date) {

        DailyNutritionSummaryResponse summary =
                nutritionSummaryService.getDailySummary(
                        profileId,
                        date
                );

        return ResponseEntity.ok(summary);
    }
}