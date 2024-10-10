package com.poly.datn.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poly.datn.entities.Category;

public interface CategoryRepository extends JpaRepository<Category,Integer>{
// Phương thức để tìm danh mục theo tên
 Optional<Category> findByCategoryName(String categoryName);
}
