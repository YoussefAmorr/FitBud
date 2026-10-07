import { useEffect, useState, type FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { ApiError, apiRequest } from '../api/api'
import type {
    CreateFoodLogRequest,
    CreateFoodRequest,
    ExternalFood,
    Food,
    FoodLog,
    MealType,
    UserProfile,
} from '../types'

function FoodSearchPage() {
    const navigate = useNavigate()

    const [profile, setProfile] = useState<UserProfile | null>(null)

    const [query, setQuery] = useState('')
    const [foods, setFoods] = useState<ExternalFood[]>([])
    const [selectedFood, setSelectedFood] =
        useState<ExternalFood | null>(null)

    const [mealType, setMealType] =
        useState<MealType>('BREAKFAST')

    const [quantityGrams, setQuantityGrams] =
        useState('100')

    const [searching, setSearching] = useState(false)
    const [logging, setLogging] = useState(false)

    const [error, setError] = useState('')
    const [message, setMessage] = useState('')

    useEffect(() => {
        async function loadProfile() {
            try {
                const userProfile =
                    await apiRequest<UserProfile>('/api/profiles/me')

                setProfile(userProfile)
            } catch (err) {
                if (err instanceof ApiError && err.status === 404) {
                    navigate('/profile/setup', { replace: true })
                    return
                }

                setError(
                    err instanceof Error
                        ? err.message
                        : 'Unable to load your profile.',
                )
            }
        }

        void loadProfile()
    }, [navigate])

    async function handleSearch(
        event: FormEvent<HTMLFormElement>,
    ) {
        event.preventDefault()

        if (!query.trim()) {
            return
        }

        setSearching(true)
        setError('')
        setMessage('')
        setSelectedFood(null)

        try {
            const results =
                await apiRequest<ExternalFood[]>(
                    `/api/external-foods/search?query=${encodeURIComponent(
                        query.trim(),
                    )}`,
                )

            setFoods(results)
        } catch (err) {
            setError(
                err instanceof Error
                    ? err.message
                    : 'Unable to search foods.',
            )
        } finally {
            setSearching(false)
        }
    }

    function selectFood(food: ExternalFood) {
        setSelectedFood(food)
        setQuantityGrams('100')
        setMessage('')
        setError('')
    }

    async function handleLogFood(
        event: FormEvent<HTMLFormElement>,
    ) {
        event.preventDefault()

        if (!profile || !selectedFood) {
            return
        }

        const quantity = Number(quantityGrams)

        if (!Number.isFinite(quantity) || quantity <= 0) {
            setError('Quantity must be greater than zero.')
            return
        }

        setLogging(true)
        setError('')
        setMessage('')

        try {
            /*
             * USDA nutrients are being treated as values per
             * 100 grams, so the internal FitBud food uses a
             * 100 gram serving.
             */
            const foodRequest: CreateFoodRequest = {
                name: selectedFood.name,
                brand: selectedFood.brand,
                servingSizeGrams: 100,
                calories: selectedFood.calories,
                proteinGrams: selectedFood.proteinGrams,
                carbohydrateGrams:
                selectedFood.carbohydrateGrams,
                fatGrams: selectedFood.fatGrams,
            }

            const createdFood = await apiRequest<Food>(
                '/api/foods',
                {
                    method: 'POST',
                    body: JSON.stringify(foodRequest),
                },
            )

            const now = new Date()

            /*
             * Spring LocalDateTime expects a local date/time
             * without a trailing UTC "Z".
             */
            const eatenAt = [
                now.getFullYear(),
                '-',
                String(now.getMonth() + 1).padStart(2, '0'),
                '-',
                String(now.getDate()).padStart(2, '0'),
                'T',
                String(now.getHours()).padStart(2, '0'),
                ':',
                String(now.getMinutes()).padStart(2, '0'),
                ':',
                String(now.getSeconds()).padStart(2, '0'),
            ].join('')

            const logRequest: CreateFoodLogRequest = {
                foodId: createdFood.id,
                mealType,
                quantityGrams: quantity,
                eatenAt,
            }

            await apiRequest<FoodLog>(
                `/api/profiles/${profile.id}/food-logs`,
                {
                    method: 'POST',
                    body: JSON.stringify(logRequest),
                },
            )

            setMessage(
                `${selectedFood.name} was added to your ${mealType.toLowerCase()}.`,
            )

            setSelectedFood(null)
            setQuantityGrams('100')
        } catch (err) {
            setError(
                err instanceof Error
                    ? err.message
                    : 'Unable to log this food.',
            )
        } finally {
            setLogging(false)
        }
    }

    return (
        <div>
            <section className="page-heading">
                <div>
                    <p className="eyebrow">FOOD DATABASE</p>
                    <h1>Find Food</h1>

                    <p className="muted">
                        Search USDA FoodData Central and add food
                        directly to your daily log.
                    </p>
                </div>
            </section>

            <section className="content-card">
                <form
                    className="search-form"
                    onSubmit={handleSearch}
                >
                    <input
                        type="search"
                        placeholder="Search chicken, rice, avocado..."
                        value={query}
                        onChange={(event) =>
                            setQuery(event.target.value)
                        }
                    />

                    <button
                        className="primary-button"
                        disabled={searching}
                    >
                        {searching ? 'Searching...' : 'Search'}
                    </button>
                </form>

                {error && (
                    <div className="error-message">
                        {error}
                    </div>
                )}

                {message && (
                    <div className="food-success-message">
                        {message}
                    </div>
                )}
            </section>

            {selectedFood && (
                <section className="content-card log-food-card">
                    <div>
                        <p className="eyebrow">ADD TO LOG</p>
                        <h2>{selectedFood.name}</h2>

                        <p className="muted">
                            {selectedFood.brand ||
                                'USDA FoodData Central'}
                        </p>
                    </div>

                    <form
                        className="log-food-form"
                        onSubmit={handleLogFood}
                    >
                        <label>
                            Meal
                            <select
                                value={mealType}
                                onChange={(event) =>
                                    setMealType(
                                        event.target.value as MealType,
                                    )
                                }
                            >
                                <option value="BREAKFAST">
                                    Breakfast
                                </option>
                                <option value="LUNCH">
                                    Lunch
                                </option>
                                <option value="DINNER">
                                    Dinner
                                </option>
                                <option value="SNACK">
                                    Snack
                                </option>
                            </select>
                        </label>

                        <label>
                            Quantity (grams)
                            <input
                                type="number"
                                min="0.1"
                                step="0.1"
                                value={quantityGrams}
                                onChange={(event) =>
                                    setQuantityGrams(event.target.value)
                                }
                                required
                            />
                        </label>

                        <div className="selected-food-preview">
              <span>
                Estimated calories
              </span>

                            <strong>
                                {Math.round(
                                    selectedFood.calories *
                                    (Number(quantityGrams || 0) / 100),
                                )}
                            </strong>
                        </div>

                        <button
                            className="primary-button"
                            disabled={logging}
                        >
                            {logging
                                ? 'Adding food...'
                                : 'Add to food log'}
                        </button>
                    </form>
                </section>
            )}

            <section className="food-results">
                {foods.map((food) => (
                    <article
                        className="food-card"
                        key={food.externalId}
                    >
                        <div className="food-description">
                            <h3>{food.name}</h3>

                            <p className="muted">
                                {food.brand ||
                                    'USDA FoodData Central'}
                            </p>
                        </div>

                        <div className="food-macros">
              <span>
                <strong>
                  {Math.round(food.calories)}
                </strong>
                cal
              </span>

                            <span>
                <strong>
                  {food.proteinGrams.toFixed(1)}g
                </strong>
                protein
              </span>

                            <span>
                <strong>
                  {food.carbohydrateGrams.toFixed(1)}g
                </strong>
                carbs
              </span>

                            <span>
                <strong>
                  {food.fatGrams.toFixed(1)}g
                </strong>
                fat
              </span>
                        </div>

                        <button
                            type="button"
                            className="secondary-button"
                            onClick={() => selectFood(food)}
                        >
                            Add
                        </button>
                    </article>
                ))}
            </section>
        </div>
    )
}

export default FoodSearchPage