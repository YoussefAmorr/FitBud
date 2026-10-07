const API_BASE_URL =
    import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

interface RequestOptions extends RequestInit {
    authenticated?: boolean
}

export class ApiError extends Error {
    status: number

    constructor(status: number, message: string) {
        super(message)
        this.name = 'ApiError'
        this.status = status
    }
}

export async function apiRequest<T>(
    endpoint: string,
    options: RequestOptions = {},
): Promise<T> {
    const { authenticated = true, headers, ...requestOptions } = options

    const requestHeaders = new Headers(headers)

    requestHeaders.set('Content-Type', 'application/json')

    if (authenticated) {
        const token = localStorage.getItem('fitbud_token')

        if (token) {
            requestHeaders.set('Authorization', `Bearer ${token}`)
        }
    }

    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        ...requestOptions,
        headers: requestHeaders,
    })

    if (!response.ok) {
        let message = 'Something went wrong.'

        try {
            const error = await response.json()

            if (error.message) {
                message = error.message
            }
        } catch {
            // The response did not contain JSON.
        }

        throw new ApiError(response.status, message)
    }

    if (response.status === 204) {
        return undefined as T
    }

    return response.json() as Promise<T>
}