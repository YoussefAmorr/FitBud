import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { ApiError, apiRequest } from '../api/api'
import type {
    NutritionSummary,
    UserProfile,
} from '../types'

function DashboardPage() {
    const navigate = useNavigate()

    const [profile, setProfile] = useState<UserProfile | null>(null)
    const [summary, setSummary] = useState<NutritionSummary | null>(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    useEffect(() => {
        async function loadDashboard() {
            let userProfile: UserProfile

            // First determine whether the authenticated user has a profile.
            try {
                userProfile =
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

                setLoading(false)
                return
            }

            // Once the profile exists, load today's nutrition summary.
            try {
                const today = new Date().toLocaleDateString('en-CA')

                const dailySummary =
                    await apiRequest<NutritionSummary>(
                        `/api/profiles/${userProfile.id}/nutrition-summary?date=${today}`,
                    )

                setSummary(dailySummary)
            } catch (err) {
                // A missing nutrition summary does NOT mean the profile is missing.
                if (!(err instanceof ApiError && err.status === 404)) {
                    setError(
                        err instanceof Error
                            ? err.message
                            : 'Unable to load your nutrition summary.',
                    )
                }
            } finally {
                setLoading(false)
            }
        }

        void loadDashboard()
    }, [navigate])

    if (loading) {
        return (
            <section className="content-card">
                <p>Loading your FitBud dashboard...</p>
            </section>
        )
    }

    if (error) {
        return <div className="error-message">{error}</div>
    }

    return (
        <div>
            <section className="page-heading">
                <div>
                    <p className="eyebrow">TODAY</p>

                    <h1>
                        {profile
                            ? `${profile.firstName}'s Dashboard`
                            : 'Nutrition Dashboard'}
                    </h1>

                    <p className="muted">
                        Track your daily nutrition and stay on top of your goals.
                    </p>
                </div>
            </section>

            <section className="summary-grid">
                <article className="summary-card">
                    <span>Calories</span>
                    <strong>
                        {Math.round(summary?.caloriesConsumed ?? 0)}
                    </strong>
                    <p>
                        {Math.round(summary?.caloriesRemaining ?? 0)} remaining
                    </p>
                </article>

                <article className="summary-card">
                    <span>Protein</span>
                    <strong>
                        {(summary?.proteinConsumedGrams ?? 0).toFixed(1)}g
                    </strong>
                    <p>
                        {(summary?.proteinRemainingGrams ?? 0).toFixed(1)}g remaining
                    </p>
                </article>

                <article className="summary-card">
                    <span>Carbs</span>
                    <strong>
                        {(summary?.carbohydrateConsumedGrams ?? 0).toFixed(1)}g
                    </strong>
                    <p>
                        {(summary?.carbohydrateRemainingGrams ?? 0).toFixed(1)}g remaining
                    </p>
                </article>

                <article className="summary-card">
                    <span>Fat</span>
                    <strong>
                        {(summary?.fatConsumedGrams ?? 0).toFixed(1)}g
                    </strong>
                    <p>
                        {(summary?.fatRemainingGrams ?? 0).toFixed(1)}g remaining
                    </p>
                </article>
            </section>

            <section className="content-card">
                <h2>Today's targets</h2>

                {summary ? (
                    <div className="target-grid">
                        <div>
                            <span>Calories</span>
                            <strong>{summary.calorieTarget}</strong>
                        </div>

                        <div>
                            <span>Protein</span>
                            <strong>{summary.proteinTargetGrams}g</strong>
                        </div>

                        <div>
                            <span>Carbohydrates</span>
                            <strong>{summary.carbohydrateTargetGrams}g</strong>
                        </div>

                        <div>
                            <span>Fat</span>
                            <strong>{summary.fatTargetGrams}g</strong>
                        </div>
                    </div>
                ) : (
                    <p className="muted">
                        No nutrition summary is available for today.
                    </p>
                )}
            </section>
        </div>
    )
}

export default DashboardPage