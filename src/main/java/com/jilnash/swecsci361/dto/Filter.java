package com.jilnash.swecsci361.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;

@Getter
public class Filter {

    private String name;

    private String category;

    private Double maxPrice;

    @Min(value = 0, message = "Price must be greater than 0")
    private Double minPrice;

    private String farmId;
}
