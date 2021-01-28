package com.rezdy.lunch;

import com.rezdy.lunch.domain.Ingredient;
import com.rezdy.lunch.domain.Recipe;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Random;
import java.util.stream.IntStream;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;

public final class TestRecipeFactory {

    private TestRecipeFactory() {}

    public static Recipe createOmeletteRecipe() {
        Recipe omelette = createTestRecipe("Omelette");
        omelette.getIngredients().add(createTestIngredient("Milk",
                LocalDate.now().plus(10, DAYS),
                LocalDate.now().plus(1, MONTHS)));
        omelette.getIngredients().add(createTestIngredient("Eggs",
                LocalDate.now().plus(20, DAYS),
                LocalDate.now().plus(2, MONTHS)));
        return omelette;
    }

    public static Recipe createTestRecipe(String title) {
        Recipe recipe = createEmptyRecipe(title);
        final Random r = new Random();
        IntStream.rangeClosed(1, new Random().nextInt(4)).forEach(value ->
                recipe.getIngredients().add(createTestIngredient("Ingredient" + value,
                    LocalDate.now().plus(r.nextInt(28) - 12, DAYS),
                    LocalDate.now().plus(r.nextInt(12) - 5, MONTHS))));
        return recipe;
    }

    public static Recipe createEmptyRecipe(String title) {
        Recipe recipe = new Recipe();
        recipe.setTitle(title);
        recipe.setIngredients(new HashSet<>());
        return recipe;
    }

    public static Ingredient createTestIngredient(String name, LocalDate bestBefore, LocalDate useBy) {
        Ingredient ingredient = new Ingredient();
        ingredient.setTitle(name);
        ingredient.setBestBefore(bestBefore);
        ingredient.setUseBy(useBy);
        return ingredient;
    }
}
