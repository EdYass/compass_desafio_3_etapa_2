package com.EdYass.ecommerce.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

public class ProductDTO {
    @Getter
    @Setter
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @Min(value = 0, message = "Stock cannot be negative")
    private int stock;

    @NotBlank(message = "Quantity cannot be negative")
    private int quantity;

    public @NotBlank(message = "Name cannot be blank") String getName() {
        return name;
    }

    public void setName(@NotBlank(message = "Name cannot be blank") String name) {
        this.name = name;
    }

    public @NotBlank(message = "Description cannot be blank") String getDescription() {
        return description;
    }

    public void setDescription(@NotBlank(message = "Description cannot be blank") String description) {
        this.description = description;
    }

    public @Positive(message = "Price must be positive") BigDecimal getPrice() {
        return price;
    }

    public void setPrice(@Positive(message = "Price must be positive") BigDecimal price) {
        this.price = price;
    }

    @Min(value = 0, message = "Stock cannot be negative")
    public int getStock() {
        return stock;
    }

    public void setStock(@Min(value = 0, message = "Stock cannot be negative") int stock) {
        this.stock = stock;
    }

    @NotBlank(message = "Quantity cannot be negative")
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(@NotBlank(message = "Quantity cannot be negative") int quantity) {
        this.quantity = quantity;
    }
}
