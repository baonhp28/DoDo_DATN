package com.poly.datn.entities;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bill_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "bill_id")
    private Bills bill;

    @ManyToOne
    @JoinColumn(name = "skus_id")
    private Skus skus;

    @Column(name = "product_name")
    private String productName;

    private Integer quantity;

    private Double price;

    @ManyToOne
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;
}

