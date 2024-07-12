package com.EdYass.ecommerce.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class SaleProductDTO {
    @NotNull
    private Long productId;

    @NotNull
    private String name;

    @NotNull
    private String description;

    @NotNull
    private BigDecimal price;

    @NotNull
    private int quantity;

    public @NotNull BigDecimal getPrice() {
        return price;
    }

    public void setPrice(@NotNull BigDecimal price) {
        this.price = price;
    }

    public @NotNull Long getProductId() {
        return productId;
    }

    public void setProductId(@NotNull Long productId) {
        this.productId = productId;
    }

    @NotNull
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(@NotNull int quantity) {
        this.quantity = quantity;
    }

    public @NotNull String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public @NotNull String getDescription() {
        return description;
    }

    public void setDescription(@NotNull String description) {
        this.description = description;
    }
}
