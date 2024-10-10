package com.poly.datn.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poly.datn.entities.Category;
import com.poly.datn.repository.CategoryRepository;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Optional<Category> findById(Integer id) {
        return categoryRepository.findById(id);
    }

    public Category save(Category category) {
        if (categoryRepository.findByCategoryName(category.getCategoryName()).isPresent()) {
            throw new IllegalArgumentException("Tên danh mục đã tồn tại");
        }
        return categoryRepository.save(category);
    }

    public Category updateCategory(Integer id, Category categoryDetails) {
        return categoryRepository.findById(id).map(category -> {
            if (categoryRepository.findByCategoryName(categoryDetails.getCategoryName()).isPresent() && !category.getId().equals(id)) {
                throw new IllegalArgumentException("Tên danh mục đã tồn tại");
            }
            category.setCategoryName(categoryDetails.getCategoryName());
            return categoryRepository.save(category);
        }).orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với id " + id));
    }

    public void deleteById(Integer id) {
        categoryRepository.deleteById(id);
    }
}
