package com.jilnash.swecsci361.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OfferDTO {

    @NotNull(message = "Product id is required")
    @Min(value = 0, message = "Product id must be greater than 0")
    private Long productId;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be greater than 0")
    private Double price;

    private String userId;

    @NotNull(message = "Message is required")
    @NotEmpty(message = "Message is required")
    private String message;
}
