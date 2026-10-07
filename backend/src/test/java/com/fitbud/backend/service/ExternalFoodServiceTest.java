package com.fitbud.backend.service;

import com.fitbud.backend.dto.ExternalFoodResponse;
import com.fitbud.backend.external.usda.UsdaFoodClient;
import com.fitbud.backend.external.usda.UsdaFoodItem;
import com.fitbud.backend.external.usda.UsdaFoodNutrient;
import com.fitbud.backend.external.usda.UsdaFoodSearchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalFoodServiceTest {

    @Mock
    private UsdaFoodClient usdaFoodClient;

    private ExternalFoodService externalFoodService;

    @BeforeEach
    void setUp() {
        externalFoodService = new ExternalFoodService(usdaFoodClient);
    }

    @Test
    void shouldSearchAndMapUsdaFoods() {

        UsdaFoodNutrient calories = createNutrient(
                "Energy",
                165.0,
                "KCAL"
        );

        UsdaFoodNutrient protein = createNutrient(
                "Protein",
                31.0,
                "G"
        );

        UsdaFoodNutrient carbohydrates = createNutrient(
                "Carbohydrate, by difference",
                0.0,
                "G"
        );

        UsdaFoodNutrient fat = createNutrient(
                "Total lipid (fat)",
                3.6,
                "G"
        );

        UsdaFoodItem food = new UsdaFoodItem();
        food.setFdcId(12345L);
        food.setDescription("Chicken Breast");
        food.setBrandName("Test Brand");
        food.setFoodNutrients(
                List.of(
                        calories,
                        protein,
                        carbohydrates,
                        fat
                )
        );

        UsdaFoodSearchResponse usdaResponse =
                new UsdaFoodSearchResponse();

        usdaResponse.setFoods(List.of(food));

        when(usdaFoodClient.searchFoods("chicken"))
                .thenReturn(usdaResponse);

        List<ExternalFoodResponse> results =
                externalFoodService.searchFoods("chicken");

        assertEquals(1, results.size());

        ExternalFoodResponse result = results.getFirst();

        assertEquals(12345L, result.getExternalId());
        assertEquals("Chicken Breast", result.getName());
        assertEquals("Test Brand", result.getBrand());
        assertEquals(165.0, result.getCalories());
        assertEquals(31.0, result.getProteinGrams());
        assertEquals(0.0, result.getCarbohydrateGrams());
        assertEquals(3.6, result.getFatGrams());
    }

    @Test
    void shouldReturnEmptyListWhenUsdaResponseIsNull() {

        when(usdaFoodClient.searchFoods("unknown"))
                .thenReturn(null);

        List<ExternalFoodResponse> results =
                externalFoodService.searchFoods("unknown");

        assertTrue(results.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenFoodsAreNull() {

        UsdaFoodSearchResponse response =
                new UsdaFoodSearchResponse();

        response.setFoods(null);

        when(usdaFoodClient.searchFoods("unknown"))
                .thenReturn(response);

        List<ExternalFoodResponse> results =
                externalFoodService.searchFoods("unknown");

        assertTrue(results.isEmpty());
    }

    @Test
    void shouldDefaultMissingNutrientsToZero() {

        UsdaFoodItem food = new UsdaFoodItem();
        food.setFdcId(67890L);
        food.setDescription("Food Without Nutrients");
        food.setBrandName(null);
        food.setFoodNutrients(null);

        UsdaFoodSearchResponse response =
                new UsdaFoodSearchResponse();

        response.setFoods(List.of(food));

        when(usdaFoodClient.searchFoods("missing"))
                .thenReturn(response);

        List<ExternalFoodResponse> results =
                externalFoodService.searchFoods("missing");

        assertEquals(1, results.size());

        ExternalFoodResponse result = results.getFirst();

        assertEquals(0.0, result.getCalories());
        assertEquals(0.0, result.getProteinGrams());
        assertEquals(0.0, result.getCarbohydrateGrams());
        assertEquals(0.0, result.getFatGrams());
    }

    @Test
    void shouldIgnoreNullNutrientValues() {

        UsdaFoodNutrient protein = createNutrient(
                "Protein",
                null,
                "G"
        );

        UsdaFoodItem food = new UsdaFoodItem();
        food.setFdcId(11111L);
        food.setDescription("Test Food");
        food.setFoodNutrients(List.of(protein));

        UsdaFoodSearchResponse response =
                new UsdaFoodSearchResponse();

        response.setFoods(List.of(food));

        when(usdaFoodClient.searchFoods("test"))
                .thenReturn(response);

        List<ExternalFoodResponse> results =
                externalFoodService.searchFoods("test");

        assertEquals(1, results.size());
        assertEquals(0.0, results.getFirst().getProteinGrams());
    }

    private UsdaFoodNutrient createNutrient(
            String name,
            Double value,
            String unit) {

        UsdaFoodNutrient nutrient = new UsdaFoodNutrient();
        nutrient.setNutrientName(name);
        nutrient.setValue(value);
        nutrient.setUnitName(unit);

        return nutrient;
    }
}