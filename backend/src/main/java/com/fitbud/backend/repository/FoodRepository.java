package com.fitbud.backend.repository;

import com.fitbud.backend.model.Food;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodRepository extends JpaRepository<Food, Long> {

    List<Food> findByNameContainingIgnoreCase(String name);

    List<Food> findByBrandContainingIgnoreCase(String brand);
}