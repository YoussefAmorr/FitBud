export interface AuthResponse {
    token: string
    tokenType: string
}

export interface RegisterResponse {
    id: number
    email: string
    createdAt: string
}

export interface UserProfile {
    id: number
    firstName: string
    lastName: string
    email: string
    heightCm: number
    weightKg: number
    createdAt: string
}

export interface CreateUserProfileRequest {
    firstName: string
    lastName: string
    email: string
    heightCm: number
    weightKg: number
}

export interface NutritionGoal {
    id: number
    userProfileId: number
    calorieTarget: number
    proteinTargetGrams: number
    carbohydrateTargetGrams: number
    fatTargetGrams: number
    createdAt: string
}

export interface CreateNutritionGoalRequest {
    calorieTarget: number
    proteinTargetGrams: number
    carbohydrateTargetGrams: number
    fatTargetGrams: number
}

export interface ExternalFood {
    externalId: number
    name: string
    brand: string | null
    calories: number
    proteinGrams: number
    carbohydrateGrams: number
    fatGrams: number
}


export interface NutritionSummary {
    userProfileId: number
    date: string

    caloriesConsumed: number
    calorieTarget: number
    caloriesRemaining: number

    proteinConsumedGrams: number
    proteinTargetGrams: number
    proteinRemainingGrams: number

    carbohydrateConsumedGrams: number
    carbohydrateTargetGrams: number
    carbohydrateRemainingGrams: number

    fatConsumedGrams: number
    fatTargetGrams: number
    fatRemainingGrams: number
}
export type MealType =
    | 'BREAKFAST'
    | 'LUNCH'
    | 'DINNER'
    | 'SNACK'

export interface CreateFoodRequest {
    name: string
    brand: string | null
    servingSizeGrams: number
    calories: number
    proteinGrams: number
    carbohydrateGrams: number
    fatGrams: number
}

export interface Food {
    id: number
    name: string
    brand: string | null
    servingSizeGrams: number
    calories: number
    proteinGrams: number
    carbohydrateGrams: number
    fatGrams: number
    createdAt: string
}

export interface CreateFoodLogRequest {
    foodId: number
    mealType: MealType
    quantityGrams: number
    eatenAt: string
}

export interface FoodLog {
    id: number
    userProfileId: number
    foodId: number
    foodName: string
    brand: string | null
    mealType: MealType
    quantityGrams: number
    calories: number
    proteinGrams: number
    carbohydrateGrams: number
    fatGrams: number
    eatenAt: string
    createdAt: string
}