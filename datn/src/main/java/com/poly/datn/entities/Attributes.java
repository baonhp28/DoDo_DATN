package com.poly.datn.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attributes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attributes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
}

