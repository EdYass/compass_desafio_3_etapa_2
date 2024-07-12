package com.EdYass.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
public class SaleProduct {
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "sale_id")
    private Sale sale;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @NotNull
    private int quantity;

    @NotNull
    private BigDecimal price;


    public @NotNull BigDecimal getPrice() {
        return price;
    }

    public void setPrice(@NotNull BigDecimal price) {
        this.price = price;
    }

    public @NotNull Product getProduct() {
        return product;
    }

    public void setProduct(@NotNull Product product) {
        this.product = product;
    }

    @NotNull
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(@NotNull int quantity) {
        this.quantity = quantity;
    }

    public @NotNull Sale getSale() {
        return sale;
    }

    public void setSale(@NotNull Sale sale) {
        this.sale = sale;
    }
}
