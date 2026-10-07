package com.fitbud.backend.external.usda;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class UsdaFoodClient {

    private final RestClient restClient;
    private final String apiKey;

    public UsdaFoodClient(
            @Value("${fitbud.usda.base-url}") String baseUrl,
            @Value("${fitbud.usda.api-key}") String apiKey) {

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();

        this.apiKey = apiKey;
    }

    public UsdaFoodSearchResponse searchFoods(String query) {

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/foods/search")
                        .queryParam("query", query)
                        .queryParam("pageSize", 10)
                        .queryParam("api_key", apiKey)
                        .build())
                .retrieve()
                .body(UsdaFoodSearchResponse.class);
    }
}