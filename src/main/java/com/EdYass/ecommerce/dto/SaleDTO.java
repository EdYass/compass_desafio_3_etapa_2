package com.EdYass.ecommerce.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class SaleDTO {
    @NotNull(message = "Sale date cannot be null")
    private LocalDateTime date;

    @Setter
    @Getter
    @NotNull(message = "Sale products cannot be null")
    private List<SaleProductDTO> saleProducts;

    public @NotNull(message = "Sale date cannot be null") LocalDateTime getDate() {
        return date;
    }

    public void setDate(@NotNull(message = "Sale date cannot be null") LocalDateTime date) {
        this.date = date;
    }

}
