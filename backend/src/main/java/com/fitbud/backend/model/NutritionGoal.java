package com.fitbud.backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "nutrition_goals")
public class NutritionGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_profile_id", nullable = false, unique = true)
    private UserProfile userProfile;

    @Column(nullable = false)
    private Integer calorieTarget;

    @Column(nullable = false)
    private Integer proteinTargetGrams;

    @Column(nullable = false)
    private Integer carbohydrateTargetGrams;

    @Column(nullable = false)
    private Integer fatTargetGrams;

    private LocalDateTime createdAt;

    protected NutritionGoal() {
    }

    public NutritionGoal(
            UserProfile userProfile,
            Integer calorieTarget,
            Integer proteinTargetGrams,
            Integer carbohydrateTargetGrams,
            Integer fatTargetGrams) {

        this.userProfile = userProfile;
        this.calorieTarget = calorieTarget;
        this.proteinTargetGrams = proteinTargetGrams;
        this.carbohydrateTargetGrams = carbohydrateTargetGrams;
        this.fatTargetGrams = fatTargetGrams;
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

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public Integer getCalorieTarget() {
        return calorieTarget;
    }

    public void setCalorieTarget(Integer calorieTarget) {
        this.calorieTarget = calorieTarget;
    }

    public Integer getProteinTargetGrams() {
        return proteinTargetGrams;
    }

    public void setProteinTargetGrams(Integer proteinTargetGrams) {
        this.proteinTargetGrams = proteinTargetGrams;
    }

    public Integer getCarbohydrateTargetGrams() {
        return carbohydrateTargetGrams;
    }

    public void setCarbohydrateTargetGrams(Integer carbohydrateTargetGrams) {
        this.carbohydrateTargetGrams = carbohydrateTargetGrams;
    }

    public Integer getFatTargetGrams() {
        return fatTargetGrams;
    }

    public void setFatTargetGrams(Integer fatTargetGrams) {
        this.fatTargetGrams = fatTargetGrams;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}