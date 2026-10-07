import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { ApiError, apiRequest } from '../api/api'
import type {
    FoodLog,
    MealType,
    UserProfile,
} from '../types'

const mealOrder: MealType[] = [
    'BREAKFAST',
    'LUNCH',
    'DINNER',
    'SNACK',
]

const mealLabels: Record<MealType, string> = {
    BREAKFAST: 'Breakfast',
    LUNCH: 'Lunch',
    DINNER: 'Dinner',
    SNACK: 'Snacks',
}

function getLocalDate() {
    return new Date().toLocaleDateString('en-CA')
}

function FoodLogPage() {
    const navigate = useNavigate()

    const [profile, setProfile] =
        useState<UserProfile | null>(null)

    const [logs, setLogs] =
        useState<FoodLog[]>([])

    const [loading, setLoading] =
        useState(true)

    const [error, setError] =
        useState('')

    const [deletingId, setDeletingId] =
        useState<number | null>(null)

    useEffect(() => {
        let cancelled = false

        async function loadFoodLogs() {
            try {
                const userProfile =
                    await apiRequest<UserProfile>(
                        '/api/profiles/me',
                    )

                if (cancelled) {
                    return
                }

                setProfile(userProfile)

                const today = getLocalDate()

                const foodLogs =
                    await apiRequest<FoodLog[]>(
                        `/api/profiles/${userProfile.id}/food-logs?date=${today}`,
                    )

                if (cancelled) {
                    return
                }

                setLogs(foodLogs)
            } catch (err) {
                if (cancelled) {
                    return
                }

                if (
                    err instanceof ApiError &&
                    err.status === 404
                ) {
                    navigate('/profile/setup', {
                        replace: true,
                    })
                    return
                }

                setError(
                    err instanceof Error
                        ? err.message
                        : 'Unable to load your food log.',
                )
            } finally {
                if (!cancelled) {
                    setLoading(false)
                }
            }
        }

        void loadFoodLogs()

        return () => {
            cancelled = true
        }
    }, [navigate])

    async function deleteLog(logId: number) {
        if (!profile) {
            return
        }

        setDeletingId(logId)
        setError('')

        try {
            await apiRequest<void>(
                `/api/profiles/${profile.id}/food-logs/${logId}`,
                {
                    method: 'DELETE',
                },
            )

            setLogs((currentLogs) =>
                currentLogs.filter(
                    (log) => log.id !== logId,
                ),
            )
        } catch (err) {
            setError(
                err instanceof Error
                    ? err.message
                    : 'Unable to delete this food log.',
            )
        } finally {
            setDeletingId(null)
        }
    }

    const totalCalories = logs.reduce(
        (total, log) => total + log.calories,
        0,
    )

    const totalProtein = logs.reduce(
        (total, log) =>
            total + log.proteinGrams,
        0,
    )

    const totalCarbs = logs.reduce(
        (total, log) =>
            total + log.carbohydrateGrams,
        0,
    )

    const totalFat = logs.reduce(
        (total, log) =>
            total + log.fatGrams,
        0,
    )

    if (loading) {
        return (
            <section className="content-card">
                <p>Loading today's food log...</p>
            </section>
        )
    }

    return (
        <div>
            <section className="page-heading">
                <div>
                    <p className="eyebrow">
                        DAILY TRACKING
                    </p>

                    <h1>Food Log</h1>

                    <p className="muted">
                        Review everything you've eaten today.
                    </p>
                </div>
            </section>

            {error && (
                <div className="error-message">
                    {error}
                </div>
            )}

            <section className="daily-log-summary">
                <div>
                    <span>Calories</span>
                    <strong>
                        {Math.round(totalCalories)}
                    </strong>
                </div>

                <div>
                    <span>Protein</span>
                    <strong>
                        {totalProtein.toFixed(1)}g
                    </strong>
                </div>

                <div>
                    <span>Carbs</span>
                    <strong>
                        {totalCarbs.toFixed(1)}g
                    </strong>
                </div>

                <div>
                    <span>Fat</span>
                    <strong>
                        {totalFat.toFixed(1)}g
                    </strong>
                </div>
            </section>

            {logs.length === 0 ? (
                <section className="content-card empty-state">
                    <h2>No foods logged today</h2>

                    <p>
                        Find a food and add it to your log
                        to start tracking today's nutrition.
                    </p>

                    <button
                        className="primary-button"
                        onClick={() => navigate('/foods')}
                    >
                        Find food
                    </button>
                </section>
            ) : (
                <div className="meal-sections">
                    {mealOrder.map((meal) => {
                        const mealLogs = logs.filter(
                            (log) => log.mealType === meal,
                        )

                        if (mealLogs.length === 0) {
                            return null
                        }

                        return (
                            <section
                                className="content-card meal-section"
                                key={meal}
                            >
                                <h2>{mealLabels[meal]}</h2>

                                <div className="meal-log-list">
                                    {mealLogs.map((log) => (
                                        <article
                                            className="food-log-item"
                                            key={log.id}
                                        >
                                            <div>
                                                <h3>{log.foodName}</h3>

                                                <p className="muted">
                                                    {log.brand ||
                                                        'FitBud food'}
                                                    {' · '}
                                                    {log.quantityGrams.toFixed(
                                                        1,
                                                    )}
                                                    g
                                                </p>
                                            </div>

                                            <div className="logged-food-macros">
                        <span>
                          <strong>
                            {Math.round(
                                log.calories,
                            )}
                          </strong>
                          cal
                        </span>

                                                <span>
                          <strong>
                            {log.proteinGrams.toFixed(
                                1,
                            )}
                              g
                          </strong>
                          protein
                        </span>

                                                <span>
                          <strong>
                            {log.carbohydrateGrams.toFixed(
                                1,
                            )}
                              g
                          </strong>
                          carbs
                        </span>

                                                <span>
                          <strong>
                            {log.fatGrams.toFixed(
                                1,
                            )}
                              g
                          </strong>
                          fat
                        </span>
                                            </div>

                                            <button
                                                type="button"
                                                className="delete-button"
                                                disabled={
                                                    deletingId === log.id
                                                }
                                                onClick={() =>
                                                    void deleteLog(log.id)
                                                }
                                            >
                                                {deletingId === log.id
                                                    ? 'Removing...'
                                                    : 'Remove'}
                                            </button>
                                        </article>
                                    ))}
                                </div>
                            </section>
                        )
                    })}
                </div>
            )}
        </div>
    )
}

export default FoodLogPage