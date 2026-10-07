package com.fitbud.backend.service;
import com.fitbud.backend.dto.ExternalFoodResponse;
import com.fitbud.backend.external.usda.UsdaFoodItem;

import java.util.Collections;
import java.util.List;
import com.fitbud.backend.external.usda.UsdaFoodClient;
import org.springframework.stereotype.Service;

@Service
public class ExternalFoodService {

    private final UsdaFoodClient usdaFoodClient;

    public ExternalFoodService(UsdaFoodClient usdaFoodClient) {
        this.usdaFoodClient = usdaFoodClient;
    }
    public List<ExternalFoodResponse> searchFoods(String query) {

        var response = usdaFoodClient.searchFoods(query);

        if (response == null || response.getFoods() == null) {
            return Collections.emptyList();
        }

        return response.getFoods()
                .stream()
                .map(this::mapToExternalFoodResponse)
                .toList();
    }
    private ExternalFoodResponse mapToExternalFoodResponse(UsdaFoodItem food) {

        return new ExternalFoodResponse(
                food.getFdcId(),
                food.getDescription(),
                food.getBrandName(),
                getNutrientValue(food, "Energy"),
                getNutrientValue(food, "Protein"),
                getNutrientValue(food, "Carbohydrate, by difference"),
                getNutrientValue(food, "Total lipid (fat)")
        );
    }
    private Double getNutrientValue(
            UsdaFoodItem food,
            String nutrientName) {

        if (food.getFoodNutrients() == null) {
            return 0.0;
        }

        return food.getFoodNutrients()
                .stream()
                .filter(nutrient ->
                        nutrientName.equalsIgnoreCase(
                                nutrient.getNutrientName()
                        )
                )
                .map(nutrient -> nutrient.getValue())
                .filter(value -> value != null)
                .findFirst()
                .orElse(0.0);
    }
}