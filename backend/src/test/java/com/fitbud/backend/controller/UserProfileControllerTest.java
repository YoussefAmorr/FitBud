package com.fitbud.backend.controller;

import com.fitbud.backend.model.UserProfile;
import com.fitbud.backend.security.SecurityConfig;
import com.fitbud.backend.service.JwtService;
import com.fitbud.backend.service.UserProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserProfileController.class)
@Import(SecurityConfig.class)
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserProfileService userProfileService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldCreateProfileWithValidRequest() throws Exception {

        UserProfile createdProfile = mock(UserProfile.class);

        when(createdProfile.getId()).thenReturn(1L);
        when(createdProfile.getFirstName()).thenReturn("Youssef");
        when(createdProfile.getLastName()).thenReturn("Amor");
        when(createdProfile.getEmail()).thenReturn("test@example.com");
        when(createdProfile.getHeightCm()).thenReturn(177.8);
        when(createdProfile.getWeightKg()).thenReturn(90.0);
        when(createdProfile.getCreatedAt())
                .thenReturn(LocalDateTime.of(2026, 10, 6, 12, 0));

        when(userProfileService.createProfile(
                any(UserProfile.class),
                eq("test@example.com")
        )).thenReturn(createdProfile);

        mockMvc.perform(
                        post("/api/profiles")
                                .contentType("application/json")
                                .content("""
                                        {
                                          "firstName": "Youssef",
                                          "lastName": "Amor",
                                          "email": "test@example.com",
                                          "heightCm": 177.8,
                                          "weightKg": 90.0
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Youssef"))
                .andExpect(jsonPath("$.lastName").value("Amor"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.heightCm").value(177.8))
                .andExpect(jsonPath("$.weightKg").value(90.0));

        verify(userProfileService).createProfile(
                any(UserProfile.class),
                eq("test@example.com")
        );
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldRejectProfileWithInvalidEmail() throws Exception {

        mockMvc.perform(
                        post("/api/profiles")
                                .contentType("application/json")
                                .content("""
                                        {
                                          "firstName": "Youssef",
                                          "lastName": "Amor",
                                          "email": "invalid-email",
                                          "heightCm": 177.8,
                                          "weightKg": 90.0
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userProfileService);
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldRejectProfileWithNonPositiveHeight() throws Exception {

        mockMvc.perform(
                        post("/api/profiles")
                                .contentType("application/json")
                                .content("""
                                        {
                                          "firstName": "Youssef",
                                          "lastName": "Amor",
                                          "email": "test@example.com",
                                          "heightCm": 0,
                                          "weightKg": 90.0
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userProfileService);
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldGetAuthenticatedUsersProfile() throws Exception {

        UserProfile profile = mock(UserProfile.class);

        when(profile.getId()).thenReturn(1L);
        when(profile.getFirstName()).thenReturn("Youssef");
        when(profile.getLastName()).thenReturn("Amor");
        when(profile.getEmail()).thenReturn("test@example.com");
        when(profile.getHeightCm()).thenReturn(177.8);
        when(profile.getWeightKg()).thenReturn(90.0);
        when(profile.getCreatedAt())
                .thenReturn(LocalDateTime.of(2026, 10, 6, 12, 0));

        when(userProfileService.getProfileByAccountEmail(
                "test@example.com"
        )).thenReturn(Optional.of(profile));

        mockMvc.perform(get("/api/profiles/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userProfileService)
                .getProfileByAccountEmail("test@example.com");
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldReturnNotFoundWhenAuthenticatedUserHasNoProfile()
            throws Exception {

        when(userProfileService.getProfileByAccountEmail(
                "test@example.com"
        )).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/profiles/me"))
                .andExpect(status().isNotFound());

        verify(userProfileService)
                .getProfileByAccountEmail("test@example.com");
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldReturnNotFoundWhenProfileIsNotOwnedByAuthenticatedUser()
            throws Exception {

        when(userProfileService.getProfileByIdAndAccountEmail(
                99L,
                "test@example.com"
        )).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/profiles/99"))
                .andExpect(status().isNotFound());

        verify(userProfileService)
                .getProfileByIdAndAccountEmail(
                        99L,
                        "test@example.com"
                );
    }
}