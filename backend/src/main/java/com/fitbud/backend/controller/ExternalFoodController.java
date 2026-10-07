package com.fitbud.backend.controller;

import com.fitbud.backend.dto.ExternalFoodResponse;
import com.fitbud.backend.service.ExternalFoodService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/external-foods")
public class ExternalFoodController {

    private final ExternalFoodService externalFoodService;

    public ExternalFoodController(
            ExternalFoodService externalFoodService) {
        this.externalFoodService = externalFoodService;
    }

    @GetMapping("/search")
    public List<ExternalFoodResponse> searchFoods(
            @RequestParam String query) {

        return externalFoodService.searchFoods(query);
    }
}