package com.fitbud.backend.security;

import com.fitbud.backend.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET =
            "FitBudTestSecretKeyForJwtAuthenticationMustBeLongEnough123456789";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(
                SECRET,
                86400000
        );
    }

    @Test
    void shouldGenerateToken() {

        String token = jwtService.generateToken(
                "test@example.com"
        );

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void shouldExtractEmailFromToken() {

        String email = "test@example.com";

        String token = jwtService.generateToken(email);

        String extractedEmail =
                jwtService.extractEmail(token);

        assertEquals(email, extractedEmail);
    }

    @Test
    void shouldValidateValidToken() {

        String token = jwtService.generateToken(
                "test@example.com"
        );

        assertTrue(
                jwtService.isTokenValid(token)
        );
    }

    @Test
    void shouldRejectTokenSignedWithDifferentSecret() {

        JwtService differentJwtService =
                new JwtService(
                        "CompletelyDifferentFitBudSecretKeyThatIsAlsoLongEnough123456",
                        86400000
                );

        String token = jwtService.generateToken(
                "test@example.com"
        );

        assertFalse(
                differentJwtService.isTokenValid(token)
        );
    }

    @Test
    void shouldRejectExpiredToken()
            throws InterruptedException {

        JwtService shortLivedJwtService =
                new JwtService(
                        SECRET,
                        1
                );

        String token =
                shortLivedJwtService.generateToken(
                        "test@example.com"
                );

        Thread.sleep(10);

        assertFalse(
                shortLivedJwtService.isTokenValid(token)
        );
    }

    @Test
    void shouldRejectMalformedToken() {

        assertFalse(
                jwtService.isTokenValid(
                        "this-is-not-a-valid-jwt"
                )
        );
    }
}