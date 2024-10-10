package com.poly.datn.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Products {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

   @NotEmpty(message = "Tên sản phẩm không được để trống")
    @Size(max = 50, message = "Tên sản phẩm tối đa 50 kí tự")
    @Pattern(regexp = "^[a-zA-Z0-9 ]*$", message = "Tên sản phẩm không được chứa kí tự đặc biệt")
    @Column(name = "product_name", nullable = false, unique = true)
    private String productName;

    private String slug;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Size(max = 100, message = "Mô tả tối đa 100 kí tự")
    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "product")
    private List<ProductImage> images;

    @OneToMany(mappedBy = "product")
    private List<Skus> skus;

    
}

