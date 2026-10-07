package com.fitbud.backend.dto;

public class ExternalFoodResponse {

    private Long externalId;
    private String name;
    private String brand;
    private Double calories;
    private Double proteinGrams;
    private Double carbohydrateGrams;
    private Double fatGrams;

    public ExternalFoodResponse(
            Long externalId,
            String name,
            String brand,
            Double calories,
            Double proteinGrams,
            Double carbohydrateGrams,
            Double fatGrams) {

        this.externalId = externalId;
        this.name = name;
        this.brand = brand;
        this.calories = calories;
        this.proteinGrams = proteinGrams;
        this.carbohydrateGrams = carbohydrateGrams;
        this.fatGrams = fatGrams;
    }

    public Long getExternalId() {
        return externalId;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public Double getCalories() {
        return calories;
    }

    public Double getProteinGrams() {
        return proteinGrams;
    }

    public Double getCarbohydrateGrams() {
        return carbohydrateGrams;
    }

    public Double getFatGrams() {
        return fatGrams;
    }
}