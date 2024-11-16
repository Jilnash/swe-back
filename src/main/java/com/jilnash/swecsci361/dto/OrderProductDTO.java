package com.jilnash.swecsci361.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderProductDTO {

    @NotNull(message = "Product id is required")
    @Min(value = 1, message = "Product id should be greater than 0")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity should be greater than 0")
    private Double quantity;

    @NotNull(message = "Sold price is required")
    @Min(value = 0, message = "Sold price should be greater than 0")
    private Double soldPrice;
}
