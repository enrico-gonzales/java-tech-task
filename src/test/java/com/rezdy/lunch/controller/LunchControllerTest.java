package com.rezdy.lunch.controller;

import com.rezdy.lunch.TestRecipeFactory;
import com.rezdy.lunch.domain.Ingredient;
import com.rezdy.lunch.domain.Recipe;
import com.rezdy.lunch.service.LunchService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LunchController.class)
class LunchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LunchService mockLunchService;

    @BeforeEach
    public void init() {
        Mockito.reset(mockLunchService);
    }

    @TestConfiguration
    static class LunchControllerTestContextConfiguration {

        @Bean
        public LunchService lunchService() {
            return mock(LunchService.class);
        }

    }


    @Test
    public void returns404WhenRecipeNotFoundByTitle() throws Exception {
        when(mockLunchService.getRecipeByTitle("Omelette")).thenReturn(Optional.empty());

        mockMvc.perform(get("/recipe/Omelette")).andExpect(status().isNotFound());

        verify(mockLunchService).getRecipeByTitle("Omelette");
    }

    @Test
    public void returnsMatchingRecipeByTitle() throws Exception {
        Recipe omelette = TestRecipeFactory.createOmeletteRecipe();
        Optional<Recipe> found = Optional.of(omelette);

        when(mockLunchService.getRecipeByTitle("Omelette")).thenReturn(found);
        mockMvc.perform(get("/recipe/Omelette"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Omelette"))
                .andExpect(jsonPath("$.ingredients", hasSize(omelette.getIngredients().size())))
                .andExpect(jsonPath("$.ingredients[*].title", hasItems(omelette.getIngredients().stream().map(Ingredient::getTitle).toArray())));

        verify(mockLunchService).getRecipeByTitle("Omelette");
    }

    @Test
    public void returns400UponInvalidRequest() throws Exception {
        mockMvc.perform(get("/lunch").param("date", "1-1-2020"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Invalid date parameter"));
    }

    @Test
    public void returns400IfNoValidParameters() throws Exception {
        mockMvc.perform(get("/recipe")).andExpect(status().isBadRequest());
        mockMvc.perform(get("/recipe/")).andExpect(status().isBadRequest());
        mockMvc.perform(get("/lunch")).andExpect(status().isBadRequest());
    }

    @Test
    public void returnsListOfRecipeWithoutExcludedIngredients() throws Exception {
        Recipe omelette = TestRecipeFactory.createOmeletteRecipe();
        Recipe r2 = TestRecipeFactory.createTestRecipe("recipe2");
        List<Recipe> result = Arrays.asList(omelette, r2);

        List<String> excludeIngredients = Arrays.asList("ing1", "ing2");
        when(mockLunchService.getRecipeByExcludedIngredients(excludeIngredients)).thenReturn(result);
        mockMvc.perform(get("/recipe").param("excludedIngredient", "ing1", "ing2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].title").value("Omelette"))
                .andExpect(jsonPath("$.[0].ingredients[*].title", hasItems(omelette.getIngredients().stream().map(Ingredient::getTitle).toArray())))
                .andExpect(jsonPath("$.[1].title").value("recipe2"))
                .andExpect(jsonPath("$.[1].ingredients[*].title", hasItems(r2.getIngredients().stream().map(Ingredient::getTitle).toArray())));

        verify(mockLunchService).getRecipeByExcludedIngredients(excludeIngredients);
    }

    @Test
    public void returnsEmptyListIfNoRecipeWithoutExcludedIngredients() throws Exception {
        when(mockLunchService.getRecipeByExcludedIngredients(any())).thenReturn(Lists.emptyList());
        mockMvc.perform(get("/recipe").param("excludedIngredient", "ing1", "ing2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").isEmpty());

        verify(mockLunchService).getRecipeByExcludedIngredients(any());
    }

    @Test
    public void returnsListOfRecipeByDate() throws Exception {
        Recipe omelette = TestRecipeFactory.createOmeletteRecipe();
        Recipe r2 = TestRecipeFactory.createTestRecipe("recipe2");
        List<Recipe> result = Arrays.asList(r2, omelette);

        LocalDate date = LocalDate.of(2020, 1, 1);
        when(mockLunchService.getNonExpiredRecipesOnDate(date)).thenReturn(result);
        mockMvc.perform(get("/lunch").param("date", "2020-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].title").value("recipe2"))
                .andExpect(jsonPath("$.[0].ingredients[*].title", hasItems(r2.getIngredients().stream().map(Ingredient::getTitle).toArray())))
                .andExpect(jsonPath("$.[1].title").value("Omelette"))
                .andExpect(jsonPath("$.[1].ingredients[*].title", hasItems(omelette.getIngredients().stream().map(Ingredient::getTitle).toArray())));

        verify(mockLunchService).getNonExpiredRecipesOnDate(date);
    }

    @Test
    public void returnsEmptyListIfNoRecipeByDate() throws Exception {
        when(mockLunchService.getNonExpiredRecipesOnDate(any())).thenReturn(Lists.emptyList());
        mockMvc.perform(get("/lunch").param("date", "2020-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").isEmpty());

        verify(mockLunchService).getNonExpiredRecipesOnDate(any());
    }
}