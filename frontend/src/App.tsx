import { Navigate, Route, Routes } from 'react-router-dom'
import './App.css'
import Layout from './components/Layout'
import ProtectedRoute from './components/ProtectedRoute'
import DashboardPage from './pages/DashboardPage'
import FoodLogPage from './pages/FoodLogPage'
import FoodSearchPage from './pages/FoodSearchPage'
import GoalsPage from './pages/GoalsPage'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import ProfileSetupPage from './pages/ProfileSetupPage'

function App() {
    return (
        <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />

            <Route
                element={
                    <ProtectedRoute>
                        <Layout />
                    </ProtectedRoute>
                }
            >
                <Route path="/dashboard" element={<DashboardPage />} />
                <Route path="/foods" element={<FoodSearchPage />} />
                <Route path="/log" element={<FoodLogPage />} />
                <Route path="/goals" element={<GoalsPage />} />
                <Route path="/profile/setup" element={<ProfileSetupPage />} />
            </Route>

            <Route path="/" element={<Navigate to="/dashboard" replace />} />
            <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
    )
}

export default App