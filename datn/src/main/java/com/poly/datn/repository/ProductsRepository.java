package com.poly.datn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poly.datn.entities.Products;

public interface ProductsRepository extends JpaRepository<Products,Integer> {
    boolean existsByProductName(String productName);
}
