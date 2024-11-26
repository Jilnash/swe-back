package com.jilnash.swecsci361.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String buyerId;

    private Double totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE)
    private List<SoldProduct> products;

    @CreationTimestamp
    private Date createdDate;
}
