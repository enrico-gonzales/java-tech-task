package com.rezdy.lunch.domain;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Recipe {

    @Id
    private String title;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "recipe_ingredient",
            joinColumns = @JoinColumn(name = "recipe"),
            inverseJoinColumns = @JoinColumn(name = "ingredient"))
    @OrderBy(value = "title")
    private Set<Ingredient> ingredients;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Set<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}
