package com.poly.datn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poly.datn.entities.Skus;

public interface SkusRepository extends JpaRepository<Skus,Integer> {
// Skus findSkuByProductId(Integer productId);
Skus findSkuByProduct_Id(Integer productId);
List<Skus> findByPriceBetween(double minPrice, double maxPrice);
}
