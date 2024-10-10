package com.poly.datn.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inbound")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inbound {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "sku_id", nullable = false)
    private Skus sku;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private LocalDateTime date; // Thay đổi từ Date thành LocalDateTime

    @Column(nullable = false)
    private double total;
}

