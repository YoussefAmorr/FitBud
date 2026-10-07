package com.fitbud.backend.controller;

import com.fitbud.backend.dto.ExternalFoodResponse;
import com.fitbud.backend.service.JwtService;
import com.fitbud.backend.security.SecurityConfig;
import com.fitbud.backend.service.ExternalFoodService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ExternalFoodController.class)
@Import(SecurityConfig.class)
class ExternalFoodControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExternalFoodService externalFoodService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldRequireAuthenticationToSearchExternalFoods()
            throws Exception {

        mockMvc.perform(
                        get("/api/external-foods/search")
                                .param("query", "chicken")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(
                        jsonPath("$.message")
                                .value("Authentication required.")
                );
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldSearchExternalFoodsWhenAuthenticated()
            throws Exception {

        ExternalFoodResponse food =
                new ExternalFoodResponse(
                        12345L,
                        "Chicken Breast",
                        "Test Brand",
                        165.0,
                        31.0,
                        0.0,
                        3.6
                );

        when(externalFoodService.searchFoods("chicken"))
                .thenReturn(List.of(food));

        mockMvc.perform(
                        get("/api/external-foods/search")
                                .param("query", "chicken")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].externalId").value(12345))
                .andExpect(
                        jsonPath("$[0].name")
                                .value("Chicken Breast")
                )
                .andExpect(
                        jsonPath("$[0].brand")
                                .value("Test Brand")
                )
                .andExpect(jsonPath("$[0].calories").value(165.0))
                .andExpect(
                        jsonPath("$[0].proteinGrams").value(31.0)
                )
                .andExpect(
                        jsonPath("$[0].carbohydrateGrams").value(0.0)
                )
                .andExpect(jsonPath("$[0].fatGrams").value(3.6));

        verify(externalFoodService).searchFoods("chicken");
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldReturnEmptyArrayWhenNoExternalFoodsAreFound()
            throws Exception {

        when(externalFoodService.searchFoods("unknownfood"))
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/api/external-foods/search")
                                .param("query", "unknownfood")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(externalFoodService)
                .searchFoods("unknownfood");
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldRejectSearchWhenQueryParameterIsMissing()
            throws Exception {

        mockMvc.perform(
                        get("/api/external-foods/search")
                )
                .andExpect(status().isBadRequest());
    }
}