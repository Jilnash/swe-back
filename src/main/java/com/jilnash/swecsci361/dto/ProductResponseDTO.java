package com.jilnash.swecsci361.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
public class ProductResponseDTO {

    private Long id;

    private String name;

    private String category;

    private Double price;

    private String unit;

    private Double quantity;

    private String description;

    private String farmId;

    private String farmName;

    private List<String> imageUrls;
}
