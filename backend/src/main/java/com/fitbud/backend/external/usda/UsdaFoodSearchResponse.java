package com.fitbud.backend.external.usda;

import java.util.List;

public class UsdaFoodSearchResponse {

    private List<UsdaFoodItem> foods;

    public List<UsdaFoodItem> getFoods() {
        return foods;
    }

    public void setFoods(List<UsdaFoodItem> foods) {
        this.foods = foods;
    }
}