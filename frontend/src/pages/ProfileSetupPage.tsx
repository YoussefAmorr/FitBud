import { useState, type FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { apiRequest } from '../api/api'
import type {
    CreateUserProfileRequest,
    UserProfile,
} from '../types'

function ProfileSetupPage() {
    const navigate = useNavigate()

    const [firstName, setFirstName] = useState('')
    const [lastName, setLastName] = useState('')
    const [email, setEmail] = useState('')
    const [heightCm, setHeightCm] = useState('')
    const [weightKg, setWeightKg] = useState('')

    const [error, setError] = useState('')
    const [loading, setLoading] = useState(false)

    async function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault()

        setError('')
        setLoading(true)

        const request: CreateUserProfileRequest = {
            firstName: firstName.trim(),
            lastName: lastName.trim(),
            email: email.trim(),
            heightCm: Number(heightCm),
            weightKg: Number(weightKg),
        }

        try {
            await apiRequest<UserProfile>('/api/profiles', {
                method: 'POST',
                body: JSON.stringify(request),
            })

            navigate('/dashboard')
        } catch (err) {
            setError(
                err instanceof Error
                    ? err.message
                    : 'Unable to create your profile.',
            )
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="profile-setup-page">
            <section className="profile-setup-card">
                <div className="auth-logo">FitBud</div>

                <p className="eyebrow">PROFILE SETUP</p>

                <h1>Tell us about yourself</h1>

                <p className="muted">
                    FitBud uses your profile to organize your nutrition goals,
                    food logs, and daily progress.
                </p>

                {error && <div className="error-message">{error}</div>}

                <form className="profile-form" onSubmit={handleSubmit}>
                    <div className="form-row">
                        <label>
                            First name
                            <input
                                type="text"
                                value={firstName}
                                onChange={(event) => setFirstName(event.target.value)}
                                required
                            />
                        </label>

                        <label>
                            Last name
                            <input
                                type="text"
                                value={lastName}
                                onChange={(event) => setLastName(event.target.value)}
                                required
                            />
                        </label>
                    </div>

                    <label>
                        Email
                        <input
                            type="email"
                            value={email}
                            onChange={(event) => setEmail(event.target.value)}
                            placeholder="you@example.com"
                            required
                        />
                    </label>

                    <div className="form-row">
                        <label>
                            Height (cm)
                            <input
                                type="number"
                                min="1"
                                step="0.1"
                                value={heightCm}
                                onChange={(event) => setHeightCm(event.target.value)}
                                required
                            />
                        </label>

                        <label>
                            Weight (kg)
                            <input
                                type="number"
                                min="1"
                                step="0.1"
                                value={weightKg}
                                onChange={(event) => setWeightKg(event.target.value)}
                                required
                            />
                        </label>
                    </div>

                    <button
                        type="submit"
                        className="primary-button"
                        disabled={loading}
                    >
                        {loading ? 'Creating profile...' : 'Continue to FitBud'}
                    </button>
                </form>
            </section>
        </div>
    )
}

export default ProfileSetupPage