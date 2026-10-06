package com.fitbud.backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "foods")
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String brand;

    @Column(nullable = false)
    private Double servingSizeGrams;

    @Column(nullable = false)
    private Double calories;

    @Column(nullable = false)
    private Double proteinGrams;

    @Column(nullable = false)
    private Double carbohydrateGrams;

    @Column(nullable = false)
    private Double fatGrams;

    private LocalDateTime createdAt;

    protected Food() {
    }

    public Food(
            String name,
            String brand,
            Double servingSizeGrams,
            Double calories,
            Double proteinGrams,
            Double carbohydrateGrams,
            Double fatGrams) {

        this.name = name;
        this.brand = brand;
        this.servingSizeGrams = servingSizeGrams;
        this.calories = calories;
        this.proteinGrams = proteinGrams;
        this.carbohydrateGrams = carbohydrateGrams;
        this.fatGrams = fatGrams;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Double getServingSizeGrams() {
        return servingSizeGrams;
    }

    public void setServingSizeGrams(Double servingSizeGrams) {
        this.servingSizeGrams = servingSizeGrams;
    }

    public Double getCalories() {
        return calories;
    }

    public void setCalories(Double calories) {
        this.calories = calories;
    }

    public Double getProteinGrams() {
        return proteinGrams;
    }

    public void setProteinGrams(Double proteinGrams) {
        this.proteinGrams = proteinGrams;
    }

    public Double getCarbohydrateGrams() {
        return carbohydrateGrams;
    }

    public void setCarbohydrateGrams(Double carbohydrateGrams) {
        this.carbohydrateGrams = carbohydrateGrams;
    }

    public Double getFatGrams() {
        return fatGrams;
    }

    public void setFatGrams(Double fatGrams) {
        this.fatGrams = fatGrams;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}