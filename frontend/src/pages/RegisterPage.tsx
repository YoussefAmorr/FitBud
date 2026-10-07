import { useState, type FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/useAuth'

function RegisterPage() {
    const { register, login } = useAuth()
    const navigate = useNavigate()

    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [confirmPassword, setConfirmPassword] = useState('')
    const [error, setError] = useState('')
    const [loading, setLoading] = useState(false)

    async function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault()

        if (password !== confirmPassword) {
            setError('Passwords do not match.')
            return
        }

        setError('')
        setLoading(true)

        try {
            await register(email, password)
            await login(email, password)
            navigate('/dashboard')
        } catch (err) {
            setError(
                err instanceof Error ? err.message : 'Unable to create account.',
            )
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="auth-page">
            <div className="auth-card">
                <div className="auth-logo">FitBud</div>

                <h1>Create your account</h1>
                <p className="muted">
                    Start tracking your calories, macros, and nutrition goals.
                </p>

                {error && <div className="error-message">{error}</div>}

                <form onSubmit={handleSubmit}>
                    <label>
                        Email
                        <input
                            type="email"
                            value={email}
                            onChange={(event) => setEmail(event.target.value)}
                            required
                        />
                    </label>

                    <label>
                        Password
                        <input
                            type="password"
                            value={password}
                            onChange={(event) => setPassword(event.target.value)}
                            required
                        />
                    </label>

                    <label>
                        Confirm password
                        <input
                            type="password"
                            value={confirmPassword}
                            onChange={(event) => setConfirmPassword(event.target.value)}
                            required
                        />
                    </label>

                    <button className="primary-button" disabled={loading}>
                        {loading ? 'Creating account...' : 'Create account'}
                    </button>
                </form>

                <p className="auth-switch">
                    Already have an account? <Link to="/login">Log in</Link>
                </p>
            </div>
        </div>
    )
}

export default RegisterPage