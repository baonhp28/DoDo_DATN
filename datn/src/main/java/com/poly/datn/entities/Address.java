package com.poly.datn.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "address")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name", nullable = false) // không thể null
    private String name;

    @Column(name = "province_id", nullable = false) // không thể null
    private Integer provinceId;

    @Column(name = "district_id", nullable = false) // không thể null
    private Integer districtId;

    @Column(name = "ward_id", nullable = false) // không thể null
    private String wardId;

    @Column(name = "detailed_address", nullable = false) // không thể null
    private String detailedAddress;

    @Column(name ="phone",nullable = false)
    private String phone;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private Users user;
}
