package com.fitbud.backend.service;

import com.fitbud.backend.model.Food;
import com.fitbud.backend.repository.FoodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FoodServiceTest {

    @Mock
    private FoodRepository foodRepository;

    private FoodService foodService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        foodService = new FoodService(foodRepository);
    }

    @Test
    void shouldCreateFood() {

        Food food = new Food(
                "Chicken Breast",
                "FitBud Foods",
                100.0,
                165.0,
                31.0,
                0.0,
                3.6
        );

        when(foodRepository.save(food))
                .thenReturn(food);

        Food result = foodService.createFood(food);

        assertSame(food, result);

        verify(foodRepository).save(food);
    }

    @Test
    void shouldGetAllFoods() {

        Food chicken = mock(Food.class);
        Food rice = mock(Food.class);

        List<Food> foods = List.of(
                chicken,
                rice
        );

        when(foodRepository.findAll())
                .thenReturn(foods);

        List<Food> result = foodService.getAllFoods();

        assertEquals(2, result.size());
        assertSame(foods, result);

        verify(foodRepository).findAll();
    }

    @Test
    void shouldGetFoodById() {

        Long foodId = 1L;

        Food food = mock(Food.class);

        when(foodRepository.findById(foodId))
                .thenReturn(Optional.of(food));

        Optional<Food> result =
                foodService.getFoodById(foodId);

        assertTrue(result.isPresent());
        assertSame(food, result.get());

        verify(foodRepository).findById(foodId);
    }

    @Test
    void shouldSearchFoodsByName() {

        String searchTerm = "chicken";

        Food chickenBreast = mock(Food.class);
        Food grilledChicken = mock(Food.class);

        List<Food> foods = List.of(
                chickenBreast,
                grilledChicken
        );

        when(foodRepository.findByNameContainingIgnoreCase(searchTerm))
                .thenReturn(foods);

        List<Food> result =
                foodService.searchFoodsByName(searchTerm);

        assertEquals(2, result.size());
        assertSame(foods, result);

        verify(foodRepository)
                .findByNameContainingIgnoreCase(searchTerm);
    }

    @Test
    void shouldSearchFoodsByBrand() {

        String brand = "FitBud";

        Food foodOne = mock(Food.class);
        Food foodTwo = mock(Food.class);

        List<Food> foods = List.of(
                foodOne,
                foodTwo
        );

        when(foodRepository.findByBrandContainingIgnoreCase(brand))
                .thenReturn(foods);

        List<Food> result =
                foodService.searchFoodsByBrand(brand);

        assertEquals(2, result.size());
        assertSame(foods, result);

        verify(foodRepository)
                .findByBrandContainingIgnoreCase(brand);
    }
}