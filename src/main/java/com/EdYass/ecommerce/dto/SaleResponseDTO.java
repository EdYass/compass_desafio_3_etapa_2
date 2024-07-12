package com.EdYass.ecommerce.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class SaleResponseDTO {
    @NotNull
    private Long id;

    @NotNull
    private LocalDateTime date;

    @NotNull
    private List<SaleProductDTO> saleProducts;

    @NotNull
    private BigDecimal total;

    public @NotNull List<SaleProductDTO> getSaleProducts() {
        return saleProducts;
    }

    public void setSaleProducts(@NotNull List<SaleProductDTO> saleProducts) {
        this.saleProducts = saleProducts;
    }

    public @NotNull BigDecimal getTotal() {
        return total;
    }

    public void setTotal(@NotNull BigDecimal total) {
        this.total = total;
    }

    public @NotNull Long getId() {
        return id;
    }

    public void setId(@NotNull Long id) {
        this.id = id;
    }

    public @NotNull LocalDateTime getDate() {
        return date;
    }

    public void setDate(@NotNull LocalDateTime date) {
        this.date = date;
    }
}
