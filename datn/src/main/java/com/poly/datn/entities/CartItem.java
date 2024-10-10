package com.poly.datn.entities;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "SkuID", referencedColumnName = "id", nullable = false)
    private Skus sku;

    @Column(name = "Quantity", nullable = false)
    private Integer quantity;
}
