package com.jilnash.swecsci361.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {

    private Long id;

    private String name;

    private String category;

    private Double price;

    private String unit;

    private Double quantity;

    private String description;

    private String farmId;

    private List<Resource> images;
}
