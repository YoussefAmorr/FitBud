import { NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/useAuth'

function Navbar() {
    const { logout } = useAuth()
    const navigate = useNavigate()

    function handleLogout() {
        logout()
        navigate('/login')
    }

    return (
        <header className="navbar">
            <NavLink to="/dashboard" className="brand">
                FitBud
            </NavLink>

            <nav className="nav-links">
                <NavLink to="/dashboard">Dashboard</NavLink>
                <NavLink to="/foods">Find Food</NavLink>
                <NavLink to="/log">Food Log</NavLink>
                <NavLink to="/goals">Goals</NavLink>

                <button className="logout-button" onClick={handleLogout}>
                    Log out
                </button>
            </nav>
        </header>
    )
}

export default Navbar