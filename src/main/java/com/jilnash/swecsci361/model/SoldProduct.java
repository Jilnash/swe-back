package com.jilnash.swecsci361.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Date;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sold_products")
public class SoldProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String category;

    private String farmId;

    private String buyerId;

    private String buyerName;

    private String status;

    private Double soldPrice;

    private Double quantity;

    @JsonIgnore
    @ManyToOne
    private Order order;

    @CreationTimestamp
    private Date soldDate;
}
