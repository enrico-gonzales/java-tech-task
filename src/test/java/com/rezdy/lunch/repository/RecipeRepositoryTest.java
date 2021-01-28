package com.rezdy.lunch.repository;

import com.rezdy.lunch.domain.Ingredient;
import com.rezdy.lunch.domain.Recipe;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(showSql = false)
public class RecipeRepositoryTest {

    @Autowired
    private RecipeRepository recipeRepository;

    @Test
    public void returnsFullRecipeIfFoundByTitle() {
        Optional<Recipe> recipe = recipeRepository.findById("Omelette");

        assertTrue(recipe.isPresent());
        assertIsOmeletteRecipe(recipe.get());
    }

    @Test
    public void returnsEmptyIfRecipeByTitleNotFound() {
        Optional<Recipe> recipe = recipeRepository.findById("fake");

        assertTrue(recipe.isEmpty());
    }

    @Test
    public void returnEmptyListIfThereIsNoRecipeWithoutExcludedIngredients() {
        List<Recipe> recipes = recipeRepository.loadByExcludedIngredients(Arrays.asList("Sausage", "Milk", "Butter", "Cucumber"));

        assertTrue(recipes.isEmpty());
    }

    @Test
    public void returnAllRecipesIfNoIngredientIsExcluded() {
        List<Recipe> recipes = recipeRepository.loadByExcludedIngredients(Arrays.asList("fake-ingredient"));

        assertEquals(5, recipes.size());
        assertTrue(isRecipeInListByTitle(recipes, "Omelette"));
        assertTrue(isRecipeInListByTitle(recipes, "Hotdog"));
        assertTrue(isRecipeInListByTitle(recipes, "Fry-up"));
        assertTrue(isRecipeInListByTitle(recipes, "Ham and Cheese Toastie"));
        assertTrue(isRecipeInListByTitle(recipes, "Salad"));
    }

    @Test
    public void returnOnlyRecipesWithoutExcludedIngredients() {
        List<Recipe> recipes = recipeRepository.loadByExcludedIngredients(Arrays.asList("Sausage", "Cheese"));

        assertEquals(2, recipes.size());
        assertTrue(isRecipeInListByTitle(recipes, "Omelette"));
        assertTrue(isRecipeInListByTitle(recipes, "Salad"));
        assertIsOmeletteRecipe(recipes.get(recipes.get(0).getTitle().equals("Omelette") ? 0 : 1));
    }

    @Test
    public void recipesByDateWhenAllIngredientsAreExpiredReturnsEmptyList() {
        List<Recipe> recipes = recipeRepository.loadByDate(LocalDate.of(2100, 1, 1));
        assertTrue(recipes.isEmpty());
    }

    @Test
    public void recipesByDateWhenSomeIngredientsAreExpiredReturnsCorrectList() {
        List<Recipe> recipes = recipeRepository.loadByDate(LocalDate.of(2012, 1, 1));
        assertEquals(2, recipes.size());
        assertTrue(isRecipeInListByTitle(recipes, "Hotdog"));
        assertTrue(isRecipeInListByTitle(recipes, "Ham and Cheese Toastie"));
    }

    @Test
    public void recipesByReturnsRecipesWithAllData() {
        List<Recipe> recipes = recipeRepository.loadByDate(LocalDate.of(1990, 1, 1));
        assertEquals(5, recipes.size());

        Recipe omelette = recipes.get(getRecipeIndexInListByTitle(recipes, "Omelette"));
        assertIsOmeletteRecipe(omelette);
    }

    private boolean isRecipeInListByTitle(List<Recipe> recipes, String title) {
        return recipes.stream().anyMatch(recipe -> recipe.getTitle().equals(title));
    }

    private int getRecipeIndexInListByTitle(List<Recipe> recipes, String title) {
        Optional<Recipe> obj = recipes.stream().filter(recipe -> recipe.getTitle().equals(title)).findFirst();
        if (obj.isEmpty()) return -1;
        return recipes.indexOf(obj.get());
    }

    private void assertIsOmeletteRecipe(Recipe recipe) {
        assertEquals("Omelette", recipe.getTitle());
        assertEquals(4, recipe.getIngredients().size());
        assertIngredient(recipe, "Spinach", LocalDate.of(2030,12,31), LocalDate.of(1999,1,1));
        assertIngredient(recipe, "Eggs", LocalDate.of(2030,12,31), LocalDate.of(2030,1,1));
        assertIngredient(recipe, "Mushrooms", LocalDate.of(2030,12,31), LocalDate.of(2010,1,1));
        assertIngredient(recipe, "Milk", LocalDate.of(2030,12,31), LocalDate.of(1999,1,1));
    }

    private void assertIngredient(Recipe recipe, String name, LocalDate bestBefore, LocalDate useBy) {
        for (Ingredient ingredient : recipe.getIngredients()) {
            if (ingredient.getTitle().equalsIgnoreCase(name) && ingredient.getBestBefore().equals(bestBefore) && ingredient.getUseBy().equals(useBy)) {
                return;
            }
        }
        Assertions.fail("Missing or mismatching ingredient: " + name);
    }
}