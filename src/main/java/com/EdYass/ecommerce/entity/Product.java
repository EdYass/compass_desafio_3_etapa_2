package com.EdYass.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
public class Product {
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @Positive
    private BigDecimal price;

    @Min(value = 0)
    private int stock;

    public @NotBlank String getName() {
        return name;
    }

    public void setName(@NotBlank String name) {
        this.name = name;
    }

    public @NotBlank String getDescription() {
        return description;
    }

    public void setDescription(@NotBlank String description) {
        this.description = description;
    }

    public @Positive BigDecimal getPrice() {
        return price;
    }

    public void setPrice(@Positive BigDecimal price) {
        this.price = price;
    }

    @Min(value = 0)
    public int getStock() {
        return stock;
    }

    public void setStock(@Min(value = 0) int stock) {
        this.stock = stock;
    }
}
