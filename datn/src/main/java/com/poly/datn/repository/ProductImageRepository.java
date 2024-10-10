package com.poly.datn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poly.datn.entities.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage,Integer>{
@Query("SELECT p FROM ProductImage p WHERE p.product.id = :productId AND p.isPrimary = true")
    ProductImage findPrimaryImageByProductId(@Param("productId") Integer productId);
}
