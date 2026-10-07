import { useEffect, useState, type FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { ApiError, apiRequest } from '../api/api'
import type {
    CreateNutritionGoalRequest,
    NutritionGoal,
    UserProfile,
} from '../types'

function GoalsPage() {
    const navigate = useNavigate()

    const [profile, setProfile] = useState<UserProfile | null>(null)
    const [existingGoal, setExistingGoal] =
        useState<NutritionGoal | null>(null)

    const [calories, setCalories] = useState('2000')
    const [protein, setProtein] = useState('150')
    const [carbs, setCarbs] = useState('200')
    const [fat, setFat] = useState('65')

    const [loading, setLoading] = useState(true)
    const [saving, setSaving] = useState(false)
    const [error, setError] = useState('')
    const [message, setMessage] = useState('')

    useEffect(() => {
        async function loadGoalsPage() {
            try {
                const userProfile =
                    await apiRequest<UserProfile>('/api/profiles/me')

                setProfile(userProfile)

                try {
                    const goal = await apiRequest<NutritionGoal>(
                        `/api/profiles/${userProfile.id}/nutrition-goals`,
                    )

                    setExistingGoal(goal)
                    setCalories(String(goal.calorieTarget))
                    setProtein(String(goal.proteinTargetGrams))
                    setCarbs(String(goal.carbohydrateTargetGrams))
                    setFat(String(goal.fatTargetGrams))
                } catch (err) {
                    if (err instanceof ApiError && err.status === 404) {
                        return
                    }

                    throw err
                }
            } catch (err) {
                if (err instanceof ApiError && err.status === 404) {
                    navigate('/profile/setup', { replace: true })
                    return
                }

                setError(
                    err instanceof Error
                        ? err.message
                        : 'Unable to load your nutrition goals.',
                )
            } finally {
                setLoading(false)
            }
        }

        void loadGoalsPage()
    }, [navigate])

    async function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault()

        if (!profile) {
            setError('Your profile could not be loaded.')
            return
        }

        if (existingGoal) {
            setError(
                'You already have a nutrition goal. Updating goals is not supported by the backend yet.',
            )
            return
        }

        const request: CreateNutritionGoalRequest = {
            calorieTarget: Number(calories),
            proteinTargetGrams: Number(protein),
            carbohydrateTargetGrams: Number(carbs),
            fatTargetGrams: Number(fat),
        }

        setSaving(true)
        setError('')
        setMessage('')

        try {
            const createdGoal = await apiRequest<NutritionGoal>(
                `/api/profiles/${profile.id}/nutrition-goals`,
                {
                    method: 'POST',
                    body: JSON.stringify(request),
                },
            )

            setExistingGoal(createdGoal)
            setMessage('Nutrition goals saved successfully.')
        } catch (err) {
            setError(
                err instanceof Error
                    ? err.message
                    : 'Unable to save your nutrition goals.',
            )
        } finally {
            setSaving(false)
        }
    }

    if (loading) {
        return (
            <section className="content-card">
                <p>Loading your nutrition goals...</p>
            </section>
        )
    }

    return (
        <div>
            <section className="page-heading">
                <div>
                    <p className="eyebrow">PERSONAL TARGETS</p>
                    <h1>Nutrition Goals</h1>
                    <p className="muted">
                        Set your daily calorie and macronutrient targets.
                    </p>
                </div>
            </section>

            <section className="content-card">
                {error && <div className="error-message">{error}</div>}

                {existingGoal && (
                    <div className="success-message">
                        Your nutrition goals are saved.
                    </div>
                )}

                <form className="goals-form" onSubmit={handleSubmit}>
                    <label>
                        Daily calories
                        <input
                            type="number"
                            min="1"
                            value={calories}
                            onChange={(event) => setCalories(event.target.value)}
                            disabled={Boolean(existingGoal)}
                            required
                        />
                    </label>

                    <label>
                        Protein (g)
                        <input
                            type="number"
                            min="0"
                            step="0.1"
                            value={protein}
                            onChange={(event) => setProtein(event.target.value)}
                            disabled={Boolean(existingGoal)}
                            required
                        />
                    </label>

                    <label>
                        Carbohydrates (g)
                        <input
                            type="number"
                            min="0"
                            step="0.1"
                            value={carbs}
                            onChange={(event) => setCarbs(event.target.value)}
                            disabled={Boolean(existingGoal)}
                            required
                        />
                    </label>

                    <label>
                        Fat (g)
                        <input
                            type="number"
                            min="0"
                            step="0.1"
                            value={fat}
                            onChange={(event) => setFat(event.target.value)}
                            disabled={Boolean(existingGoal)}
                            required
                        />
                    </label>

                    {!existingGoal && (
                        <button
                            type="submit"
                            className="primary-button"
                            disabled={saving}
                        >
                            {saving ? 'Saving...' : 'Save goals'}
                        </button>
                    )}

                    {message && (
                        <p className="success-message">{message}</p>
                    )}
                </form>
            </section>
        </div>
    )
}

export default GoalsPage