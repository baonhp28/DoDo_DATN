package com.poly.datn.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "category")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

     @NotBlank(message = "Category name không được để trống")
    @Column(name = "category_name", nullable = false, unique = true)
    private String categoryName;
}


