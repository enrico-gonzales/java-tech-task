package com.rezdy.lunch.service;

import com.rezdy.lunch.domain.Recipe;
import com.rezdy.lunch.repository.RecipeRepository;
import com.rezdy.lunch.service.impl.LunchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.rezdy.lunch.TestRecipeFactory.createTestIngredient;
import static com.rezdy.lunch.TestRecipeFactory.createTestRecipe;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LunchServiceTest {

    @InjectMocks
    private LunchServiceImpl lunchService;

    @Mock
    private RecipeRepository mockRecipeRepository;

    @BeforeEach
    public void init() {
        Mockito.reset(mockRecipeRepository);
    }

    @Test
    public void passesEmptyOptionalBack_whenNoMatchingRecipeByTitle() {
        Recipe found = mock(Recipe.class);
        when(mockRecipeRepository.findById("test")).thenReturn(Optional.of(found));

        Optional<Recipe> result = lunchService.getRecipeByTitle("test");

        verify(mockRecipeRepository).findById("test");
        assertEquals(found, result.get());
    }

    @Test
    public void passesDataFromRepositoryBack_whenFindingRecipeByTitle() {
        when(mockRecipeRepository.findById("test")).thenReturn(Optional.empty());

        Optional<Recipe> result = lunchService.getRecipeByTitle("test");

        verify(mockRecipeRepository).findById("test");
        assertTrue(result.isEmpty());
    }

    @Test
    public void usesTheRepositoryToFetchRecipesWithoutExcludedIngredientsAndPassesDataBack() {
        List<String> excludeIngredients = Arrays.asList("ing1", "ing2");
        List<Recipe> recipes = Arrays.asList(
                createTestRecipe("Recipe1"),
                createTestRecipe("Recipe2")
        );
        when(mockRecipeRepository.loadByExcludedIngredients(excludeIngredients)).thenReturn(recipes);

        List<Recipe> result = lunchService.getRecipeByExcludedIngredients(excludeIngredients);

        assertArrayEquals(recipes.toArray(), result.toArray());
        verify(mockRecipeRepository).loadByExcludedIngredients(excludeIngredients);
    }

    @Test
    public void usesTheRepositoryToFetchRecipesWithoutExcludedIngredientsAndReturnsEmptyListIfNothingFound() {
        List<String> excludeIngredients = Arrays.asList("ing1", "ing2");
        when(mockRecipeRepository.loadByExcludedIngredients(excludeIngredients)).thenReturn(new ArrayList<>());

        List<Recipe> result = lunchService.getRecipeByExcludedIngredients(excludeIngredients);

        assertTrue(result.isEmpty());
        verify(mockRecipeRepository).loadByExcludedIngredients(excludeIngredients);
    }

    @Test
    public void loadRecipeByDate_sortsProperly_whenMixOfBestBeforeAcrossGivenDate() {

        List<Recipe> recipes = getTestDataSetForSorting();

        LocalDate date = LocalDate.of(2012, 2, 2);
        when(mockRecipeRepository.loadByDate(date)).thenReturn(recipes);

        List<Recipe> result = lunchService.getNonExpiredRecipesOnDate(date);

        assertEquals(4, result.size());
        assertEquals("C-Recipe", result.get(0).getTitle());
        assertEquals("A-Recipe", result.get(1).getTitle());
        assertEquals("B-Recipe", result.get(2).getTitle());
        assertEquals("D-Recipe", result.get(3).getTitle());

        verify(mockRecipeRepository).loadByDate(date);
    }

    @Test
    public void loadRecipeByDate_sortsProperly_whenAllBestBeforeAfterGivenDate() {

        List<Recipe> recipes = getTestDataSetForSorting();

        LocalDate date = LocalDate.of(2000, 2, 2);
        when(mockRecipeRepository.loadByDate(date)).thenReturn(recipes);

        List<Recipe> result = lunchService.getNonExpiredRecipesOnDate(date);

        assertEquals(4, result.size());
        assertEquals("A-Recipe", result.get(0).getTitle());
        assertEquals("B-Recipe", result.get(1).getTitle());
        assertEquals("C-Recipe", result.get(2).getTitle());
        assertEquals("D-Recipe", result.get(3).getTitle());

        verify(mockRecipeRepository).loadByDate(date);
    }

    @Test
    public void loadRecipeByDate_sortsProperly_whenMixOfBestBeforeAcrossGivenDate_variation2() {

        List<Recipe> recipes = getTestDataSetForSorting();

        LocalDate date = LocalDate.of(2010, 2, 2);
        when(mockRecipeRepository.loadByDate(date)).thenReturn(recipes);

        List<Recipe> result = lunchService.getNonExpiredRecipesOnDate(date);

        assertEquals(4, result.size());
        assertEquals("C-Recipe", result.get(0).getTitle());
        assertEquals("D-Recipe", result.get(1).getTitle());
        assertEquals("A-Recipe", result.get(2).getTitle());
        assertEquals("B-Recipe", result.get(3).getTitle());

        verify(mockRecipeRepository).loadByDate(date);
    }

    @Test
    public void loadRecipeByDate_sortsProperly_whenAllBestBeforeBeforeGivenDate() {

        List<Recipe> recipes = getTestDataSetForSorting();

        LocalDate date = LocalDate.of(2019, 2, 2);
        when(mockRecipeRepository.loadByDate(date)).thenReturn(recipes);

        List<Recipe> result = lunchService.getNonExpiredRecipesOnDate(date);

        assertEquals(4, result.size());
        assertEquals("A-Recipe", result.get(0).getTitle());
        assertEquals("B-Recipe", result.get(1).getTitle());
        assertEquals("D-Recipe", result.get(2).getTitle());
        assertEquals("C-Recipe", result.get(3).getTitle());

        verify(mockRecipeRepository).loadByDate(date);
    }


    private List<Recipe> getTestDataSetForSorting() {
        Recipe r1 = createTestRecipe("B-Recipe");
        r1.getIngredients().add(createTestIngredient("Ing1", LocalDate.of(2010, 1, 1), LocalDate.of(2020, 1, 1)));
        r1.getIngredients().add(createTestIngredient("Ing2", LocalDate.of(2012, 1, 1), LocalDate.of(2022, 1, 1)));

        Recipe r2 = createTestRecipe("A-Recipe");
        r2.getIngredients().add(createTestIngredient("Ing1", LocalDate.of(2010, 1, 1), LocalDate.of(2022, 1, 1)));
        r2.getIngredients().add(createTestIngredient("Ing2", LocalDate.of(2012, 1, 1), LocalDate.of(2020, 1, 1)));
        r2.getIngredients().add(createTestIngredient("Ing2", LocalDate.of(2015, 1, 1), LocalDate.of(2020, 1, 1)));

        Recipe r3 = createTestRecipe("D-Recipe");
        r3.getIngredients().add(createTestIngredient("Ing3", LocalDate.of(2011, 1, 1), LocalDate.of(2022, 1, 1)));

        Recipe r4 = createTestRecipe("C-Recipe");
        r4.getIngredients().add(createTestIngredient("Ing1", LocalDate.of(2015, 1, 1), LocalDate.of(2021, 1, 1)));
        r4.getIngredients().add(createTestIngredient("Ing2", LocalDate.of(2015, 1, 1), LocalDate.of(2022, 1, 1)));

        return Arrays.asList(r1, r2, r3, r4);
    }
}