import { useState, type FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/useAuth'

function LoginPage() {
    const { login } = useAuth()
    const navigate = useNavigate()

    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [error, setError] = useState('')
    const [loading, setLoading] = useState(false)

    async function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault()

        setError('')
        setLoading(true)

        try {
            await login(email, password)
            navigate('/dashboard')
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Unable to log in.')
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="auth-page">
        <div className="auth-card">
        <div className="auth-logo">FitBud</div>

            <h1>Welcome back</h1>
    <p className="muted">Log in to continue tracking your nutrition.</p>

    {error && <div className="error-message">{error}</div>}

        <form onSubmit={handleSubmit}>
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

        <label>
        Password
        <input
        type="password"
        value={password}
        onChange={(event) => setPassword(event.target.value)}
        placeholder="Enter your password"
        required
        />
        </label>

        <button className="primary-button" disabled={loading}>
        {loading ? 'Logging in...' : 'Log in'}
        </button>
        </form>

        <p className="auth-switch">
        New to FitBud? <Link to="/register">Create an account</Link>
    </p>
    </div>
    </div>
    )
    }

    export default LoginPage