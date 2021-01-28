package com.rezdy.lunch.repository;

import com.rezdy.lunch.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, String> {

    @Query("SELECT DISTINCT r FROM Recipe r JOIN FETCH r.ingredients WHERE NOT r.title = ALL " +
            "(SELECT DISTINCT r2.title FROM Recipe r2 join r2.ingredients i WHERE i.useBy < :date)")
    List<Recipe> loadByDate(@Param("date") LocalDate date);

    @Query("SELECT DISTINCT r FROM Recipe r JOIN FETCH r.ingredients WHERE NOT r.title = ALL " +
            "(SELECT r2 FROM Recipe r2 join r2.ingredients i WHERE r2.title = r.title AND i.title IN (:excludedIngredients))")
    List<Recipe> loadByExcludedIngredients(@Param("excludedIngredients") List<String> excludedIngredients);
}
