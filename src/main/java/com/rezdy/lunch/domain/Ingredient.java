package com.rezdy.lunch.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
public class Ingredient {

    @Id
    private String title;

    private LocalDate bestBefore;

    private LocalDate useBy;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getBestBefore() {
        return bestBefore;
    }

    public void setBestBefore(LocalDate bestBefore) {
        this.bestBefore = bestBefore;
    }

    public LocalDate getUseBy() {
        return useBy;
    }

    public void setUseBy(LocalDate useBy) {
        this.useBy = useBy;
    }
}
