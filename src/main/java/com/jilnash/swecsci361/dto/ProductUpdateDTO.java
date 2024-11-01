package com.jilnash.swecsci361.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductUpdateDTO {

    @NotNull(message = "Id is required")
    @Min(value = 1, message = "Id must be greater than 0")
    private Long id;

    @NotNull(message = "Name is required")
    @NotEmpty(message = "Name is required")
    private String name;

    @NotNull(message = "Category is required")
    @NotEmpty(message = "Category is required")
    private String category;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be greater than 0")
    private Double price;

    @NotNull(message = "Unit is required")
    private String unit;

    @NotNull(message = "Quantity is required")
    private Double quantity;

    @NotNull(message = "Description is required")
    @NotEmpty(message = "Description is required")
    @Size(min = 10, message = "Description must be at least 10 characters")
    private String description;
}
