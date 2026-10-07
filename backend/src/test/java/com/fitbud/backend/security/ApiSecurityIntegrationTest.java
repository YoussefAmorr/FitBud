package com.fitbud.backend.security;

import com.fitbud.backend.controller.HealthController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import com.fitbud.backend.service.JwtService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = HealthController.class)
@Import(SecurityConfig.class)
class ApiSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldAllowHealthEndpointWithoutAuthentication() throws Exception {

        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectProtectedProfileEndpointWithoutAuthentication()
            throws Exception {

        mockMvc.perform(get("/api/profiles/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json(
                        """
                        {
                          "status": 401,
                          "message": "Authentication required."
                        }
                        """
                ));
    }

    @Test
    void shouldRejectProtectedFoodEndpointWithoutAuthentication()
            throws Exception {

        mockMvc.perform(get("/api/foods"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json(
                        """
                        {
                          "status": 401,
                          "message": "Authentication required."
                        }
                        """
                ));
    }

    @Test
    void shouldRejectProtectedNutritionSummaryEndpointWithoutAuthentication()
            throws Exception {

        mockMvc.perform(
                        get("/api/profiles/1/nutrition-summary")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(content().json(
                        """
                        {
                          "status": 401,
                          "message": "Authentication required."
                        }
                        """
                ));
    }
}