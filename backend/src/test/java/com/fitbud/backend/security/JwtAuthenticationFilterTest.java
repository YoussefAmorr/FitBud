package com.fitbud.backend.security;

import com.fitbud.backend.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtService jwtService;
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {

        jwtService = mock(JwtService.class);

        jwtAuthenticationFilter =
                new JwtAuthenticationFilter(jwtService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);

        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAuthenticateRequestWithValidBearerToken()
            throws Exception {

        String token = "valid-token";
        String email = "test@example.com";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        when(jwtService.isTokenValid(token))
                .thenReturn(true);

        when(jwtService.extractEmail(token))
                .thenReturn(email);

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assertNotNull(authentication);
        assertTrue(authentication.isAuthenticated());
        assertEquals(email, authentication.getPrincipal());

        verify(jwtService).isTokenValid(token);
        verify(jwtService).extractEmail(token);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWhenAuthorizationHeaderIsMissing()
            throws Exception {

        when(request.getHeader("Authorization"))
                .thenReturn(null);

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verifyNoInteractions(jwtService);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWhenAuthorizationHeaderIsNotBearer()
            throws Exception {

        when(request.getHeader("Authorization"))
                .thenReturn("Basic credentials");

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verifyNoInteractions(jwtService);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWhenTokenIsInvalid()
            throws Exception {

        String token = "invalid-token";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        when(jwtService.isTokenValid(token))
                .thenReturn(false);

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verify(jwtService).isTokenValid(token);
        verify(jwtService, never()).extractEmail(token);
        verify(filterChain).doFilter(request, response);
    }
}