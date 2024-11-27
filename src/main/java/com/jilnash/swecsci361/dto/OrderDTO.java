package com.jilnash.swecsci361.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderDTO {

    private String buyerId;

    private String buyerName;

    @Valid
    @NotNull(message = "Products are required")
    @NotEmpty(message = "Products are required")
    private List<OrderProductDTO> products;
}
