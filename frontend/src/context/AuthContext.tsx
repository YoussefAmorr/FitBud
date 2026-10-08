import {
    useState,
    type ReactNode,
} from 'react'
import { apiRequest } from '../api/api'
import type {
    AuthResponse,
    RegisterResponse,
} from '../types'
import { AuthContext } from './AuthContextDefinition'

export function AuthProvider({
                                 children,
                             }: {
    children: ReactNode
}) {
    const [token, setToken] = useState<string | null>(
        () => localStorage.getItem('fitbud_token'),
    )

    async function login(
        email: string,
        password: string,
    ) {
        const response =
            await apiRequest<AuthResponse>(
                '/api/auth/login',
                {
                    method: 'POST',
                    authenticated: false,
                    body: JSON.stringify({
                        email,
                        password,
                    }),
                },
            )

        localStorage.setItem('fitbud_token', response.token)
        setToken(response.token)
    }

    async function register(
        email: string,
        password: string,
    ): Promise<RegisterResponse> {
        return apiRequest<RegisterResponse>(
            '/api/auth/register',
            {
                method: 'POST',
                authenticated: false,
                body: JSON.stringify({
                    email,
                    password,
                }),
            },
        )
    }

    function logout() {
        localStorage.removeItem('fitbud_token')
        setToken(null)
    }

    return (
        <AuthContext.Provider
            value={{
                token,
                isAuthenticated: Boolean(token),
                login,
                register,
                logout,
            }}
        >
            {children}
        </AuthContext.Provider>
    )
}