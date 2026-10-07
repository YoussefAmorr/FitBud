package com.fitbud.backend.controller;

import com.fitbud.backend.security.JwtAuthenticationFilter;
import com.fitbud.backend.security.SecurityConfig;
import com.fitbud.backend.service.AuthService;
import com.fitbud.backend.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fitbud.backend.model.UserAccount;

import java.time.LocalDateTime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldRejectRegistrationWithInvalidEmail() throws Exception {

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType("application/json")
                                .content("""
                                        {
                                          "email": "not-an-email",
                                          "password": "password123"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void shouldRejectRegistrationWithBlankEmail() throws Exception {

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType("application/json")
                                .content("""
                                        {
                                          "email": "",
                                          "password": "password123"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void shouldRejectRegistrationWithShortPassword() throws Exception {

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType("application/json")
                                .content("""
                                        {
                                          "email": "test@example.com",
                                          "password": "short"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void shouldRejectRegistrationWithBlankPassword() throws Exception {

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType("application/json")
                                .content("""
                                        {
                                          "email": "test@example.com",
                                          "password": ""
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void shouldRejectLoginWithInvalidEmail() throws Exception {

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType("application/json")
                                .content("""
                                        {
                                          "email": "invalid-email",
                                          "password": "password123"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void shouldRejectLoginWithBlankPassword() throws Exception {

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType("application/json")
                                .content("""
                                        {
                                          "email": "test@example.com",
                                          "password": ""
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void shouldRejectRegistrationWithMissingEmail() throws Exception {

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType("application/json")
                                .content("""
                                        {
                                          "password": "password123"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void shouldRejectLoginWithMissingEmail() throws Exception {

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType("application/json")
                                .content("""
                                        {
                                          "password": "password123"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }
    @Test
    void shouldRegisterUserWithValidRequest() throws Exception {

        UserAccount userAccount = mock(UserAccount.class);

        when(userAccount.getId()).thenReturn(1L);
        when(userAccount.getEmail()).thenReturn("test@example.com");
        when(userAccount.getCreatedAt())
                .thenReturn(LocalDateTime.of(2026, 10, 6, 12, 0));

        when(authService.register(
                "test@example.com",
                "password123"
        )).thenReturn(userAccount);

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType("application/json")
                                .content("""
                                    {
                                      "email": "test@example.com",
                                      "password": "password123"
                                    }
                                    """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(authService).register(
                "test@example.com",
                "password123"
        );
    }

    @Test
    void shouldLoginUserWithValidRequest() throws Exception {

        when(authService.login(
                "test@example.com",
                "password123"
        )).thenReturn("test-jwt-token");

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType("application/json")
                                .content("""
                                    {
                                      "email": "test@example.com",
                                      "password": "password123"
                                    }
                                    """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));

        verify(authService).login(
                "test@example.com",
                "password123"
        );
    }
}