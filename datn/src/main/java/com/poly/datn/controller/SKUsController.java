package com.poly.datn.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.poly.datn.entities.Skus;
import com.poly.datn.repository.SkusRepository;
import com.poly.datn.service.SKUService;

@RestController
@RequestMapping("/api")
public class SKUsController {

    @Autowired
    SKUService skuService;
    @Autowired
    SkusRepository skusRepository;

@PostMapping("/searchSkusByPrice")
public ResponseEntity<Object> getSkusByPrice(
        @RequestParam int minPrice, 
        @RequestParam int maxPrice) {
    
    // Kiểm tra tính hợp lệ của minPrice và maxPrice
    if (minPrice < 0) {
        String error="Giá phải lớn hơn 0";
        return ResponseEntity.badRequest().body(error);
    }
    
    if (maxPrice < minPrice) {
        String error="Giá lớn nhất phải lớn hơn giá nhỏ nhất";
        return ResponseEntity.badRequest().body(error);
    }
    
    // Truy vấn dịch vụ để lấy danh sách SKUs
    List<Skus> skus = skuService.getSKUByPrice(minPrice, maxPrice);
    
    // Kiểm tra nếu không có SKUs
    if (skus.isEmpty()) {
        String error="Không tìm thấy SKUs hợp lệ";
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    return ResponseEntity.ok(skus);
}

    @GetMapping("/allSkus")
    public List<Skus> getAllSKUs(){
        return skusRepository.findAll();
    }
}