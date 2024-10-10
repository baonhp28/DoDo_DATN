package com.poly.datn.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbound")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Outbound {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "sku_id", nullable = false)
    private Skus sku;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime date;

    @Column(nullable = false)
    private double total;
}
