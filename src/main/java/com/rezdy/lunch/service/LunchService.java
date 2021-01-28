package com.rezdy.lunch.service;

import com.rezdy.lunch.domain.Recipe;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LunchService {
    @Transactional
    List<Recipe> getNonExpiredRecipesOnDate(LocalDate date);

    @Transactional
    Optional<Recipe> getRecipeByTitle(String title);

    @Transactional
    List<Recipe> getRecipeByExcludedIngredients(List<String> excludedIngredients);
}
