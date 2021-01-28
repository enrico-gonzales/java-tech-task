package com.rezdy.lunch.controller;

import com.rezdy.lunch.domain.Recipe;
import com.rezdy.lunch.service.LunchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class LunchController {

    private LunchService lunchService;

    @Autowired
    public LunchController(LunchService lunchService) {
        this.lunchService = lunchService;
    }

    @GetMapping("/lunch")
    public List<Recipe> getRecipesByDate(@RequestParam(value = "date") String date) {
        return lunchService.getNonExpiredRecipesOnDate(LocalDate.parse(date));
    }

    @GetMapping("/recipe/{title}")
    public ResponseEntity<Recipe> getRecipeByTitle(@PathVariable(value = "title") String title) {
        return ResponseEntity.of(lunchService.getRecipeByTitle(title));
    }

    @GetMapping("/recipe")
    public List<Recipe> getRecipesWithoutGivenIngredients(@RequestParam(value = "excludedIngredient") List<String> excludedIngredients) {
        return lunchService.getRecipeByExcludedIngredients(excludedIngredients);
    }

}
