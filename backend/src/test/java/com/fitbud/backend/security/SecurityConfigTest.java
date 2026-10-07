package com.fitbud.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {

        JwtAuthenticationFilter jwtAuthenticationFilter =
                mock(JwtAuthenticationFilter.class);

        securityConfig = new SecurityConfig(
                jwtAuthenticationFilter
        );
    }

    @Test
    void shouldProvideBCryptPasswordEncoder() {

        assertNotNull(securityConfig.passwordEncoder());

        assertEquals(
                "BCryptPasswordEncoder",
                securityConfig.passwordEncoder()
                        .getClass()
                        .getSimpleName()
        );
    }
}