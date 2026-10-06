package com.fitbud.backend.controller;

import com.fitbud.backend.dto.CreateFoodRequest;
import com.fitbud.backend.dto.FoodResponse;
import com.fitbud.backend.model.Food;
import com.fitbud.backend.service.FoodService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
public class FoodController {

    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @PostMapping
    public ResponseEntity<FoodResponse> createFood(
            @Valid @RequestBody CreateFoodRequest request) {

        Food food = new Food(
                request.name(),
                request.brand(),
                request.servingSizeGrams(),
                request.calories(),
                request.proteinGrams(),
                request.carbohydrateGrams(),
                request.fatGrams()
        );

        Food createdFood = foodService.createFood(food);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(createdFood));
    }

    @GetMapping
    public ResponseEntity<List<FoodResponse>> getFoods(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String brand) {

        List<Food> foods;

        if (name != null && !name.isBlank()) {
            foods = foodService.searchFoodsByName(name);
        } else if (brand != null && !brand.isBlank()) {
            foods = foodService.searchFoodsByBrand(brand);
        } else {
            foods = foodService.getAllFoods();
        }

        List<FoodResponse> response = foods
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodResponse> getFoodById(
            @PathVariable Long id) {

        return foodService
                .getFoodById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private FoodResponse toResponse(Food food) {
        return new FoodResponse(
                food.getId(),
                food.getName(),
                food.getBrand(),
                food.getServingSizeGrams(),
                food.getCalories(),
                food.getProteinGrams(),
                food.getCarbohydrateGrams(),
                food.getFatGrams(),
                food.getCreatedAt()
        );
    }
}