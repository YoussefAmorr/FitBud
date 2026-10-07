package com.fitbud.backend.external.usda;

import java.util.List;

public class UsdaFoodItem {

    private Long fdcId;
    private String description;
    private String brandName;
    private List<UsdaFoodNutrient> foodNutrients;

    public Long getFdcId() {
        return fdcId;
    }

    public void setFdcId(Long fdcId) {
        this.fdcId = fdcId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public List<UsdaFoodNutrient> getFoodNutrients() {
        return foodNutrients;
    }

    public void setFoodNutrients(List<UsdaFoodNutrient> foodNutrients) {
        this.foodNutrients = foodNutrients;
    }
}