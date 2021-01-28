package com.rezdy.lunch.service.impl;

import com.rezdy.lunch.domain.Ingredient;
import com.rezdy.lunch.domain.Recipe;
import com.rezdy.lunch.repository.RecipeRepository;
import com.rezdy.lunch.service.LunchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LunchServiceImpl implements LunchService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Override
    @Transactional
    public List<Recipe> getNonExpiredRecipesOnDate(LocalDate date) {
        List<Recipe> recipes = loadRecipes(date);
        return sortRecipes(recipes, date);
    }

    private List<Recipe> sortRecipes(List<Recipe> recipes, final LocalDate date) {
        List<Recipe> recipesSorted = new ArrayList<>();
        if (recipes != null) {
            recipesSorted.addAll(recipes);
            recipesSorted.sort((o1, o2) -> compareRecipesBasedOnBestBeforeDateAndTitle(date, o1, o2));
        }
        return recipesSorted;
    }

    private int compareRecipesBasedOnBestBeforeDateAndTitle(LocalDate date, Recipe o1, Recipe o2) {
        Optional<LocalDate> olderExpiredBestBefore1 = getOldestPastBestBeforeDateFromRecipeIngredients(date, o1);
        Optional<LocalDate> olderExpiredBestBefore2 = getOldestPastBestBeforeDateFromRecipeIngredients(date, o2);
        // recipes with an ingredient past its "best before" at the bottom at the list, sorted by
        // "best before" desc, recipe title asc
        if (olderExpiredBestBefore1.isPresent() && olderExpiredBestBefore2.isEmpty()) {
            return 1;
        } else if (olderExpiredBestBefore1.isEmpty() && olderExpiredBestBefore2.isPresent()) {
            return -1;
        } else if (olderExpiredBestBefore1.isPresent() && olderExpiredBestBefore2.isPresent()) {
            int dateComparison = olderExpiredBestBefore1.get().compareTo(olderExpiredBestBefore2.get());
            if (dateComparison != 0) return dateComparison;
        }
        return o1.getTitle().compareTo(o2.getTitle());
    }

    private Optional<LocalDate> getOldestPastBestBeforeDateFromRecipeIngredients(LocalDate date, Recipe o1) {
        return o1.getIngredients().stream()
                .map(Ingredient::getBestBefore)
                .filter(bestBefore -> bestBefore != null && date.isAfter(bestBefore))
                .min(LocalDate::compareTo);
    }

    private List<Recipe> loadRecipes(LocalDate date) {
        return recipeRepository.loadByDate(date);
    }

    @Override
    @Transactional
    public Optional<Recipe> getRecipeByTitle(String title) {
        return recipeRepository.findById(title);
    }

    @Override
    @Transactional
    public List<Recipe> getRecipeByExcludedIngredients(List<String> excludedIngredients) {
        return recipeRepository.loadByExcludedIngredients(excludedIngredients);
    }
}
