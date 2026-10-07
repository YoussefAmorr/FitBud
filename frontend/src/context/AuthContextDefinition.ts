import { createContext } from 'react'
import type { RegisterResponse } from '../types'

export interface AuthContextType {
    token: string | null
    isAuthenticated: boolean
    login: (email: string, password: string) => Promise<void>
    register: (
        email: string,
        password: string,
    ) => Promise<RegisterResponse>
    logout: () => void
}

export const AuthContext =
    createContext<AuthContextType | undefined>(undefined)