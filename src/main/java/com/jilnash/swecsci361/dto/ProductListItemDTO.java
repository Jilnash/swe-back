package com.jilnash.swecsci361.dto;

import lombok.*;

@Builder
@Data
public class ProductListItemDTO {

    private Long id;

    private String name;

    private String category;

    private Double price;

    private String unit;

    private Double quantity;

    private String description;

    private String farmId;

    private String farmName;

    private String imageURL;
}
