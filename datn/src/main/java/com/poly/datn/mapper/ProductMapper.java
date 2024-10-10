package com.poly.datn.mapper;

import com.poly.datn.dto.ProductDTO;
import com.poly.datn.entities.Products;
import com.poly.datn.entities.ProductImage;
import com.poly.datn.entities.Skus;
import com.poly.datn.entities.Inbound;

import java.util.List;

public class ProductMapper {

    public static ProductDTO toProductDTO(Products product, ProductImage productImage, Skus skus, List<Inbound> inbounds) {
        // Các giá trị mặc định nếu các thực thể liên quan là null
        String imageUrl = (productImage != null && productImage.getImageUrl() != null) ? productImage.getImageUrl() : "default-image-url.jpg"; // Thay thế với URL mặc định nếu không có ảnh
        // Các giá trị mặc định nếu các thực thể liên quan là null
        // String imageUrl = (productImage != null) ? productImage.getImageUrl() : null;
        double price = (skus != null) ? skus.getPrice() : 0.0;
        String categoryName = (product.getCategory() != null) ? product.getCategory().getCategoryName() : "Unknown Category";
    
        // Tính tổng số lượng từ danh sách inbounds, đảm bảo kiểm tra null
        int totalQuantity = (inbounds != null) ? inbounds.stream()
            .filter(inbound -> inbound.getSku().equals(skus)) // Lọc inbounds theo SKU
            .mapToInt(Inbound::getQuantity) // Lấy số lượng
            .sum() : 0; // Tính tổng số lượng hoặc trả về 0 nếu inbounds là null
    
        return new ProductDTO(
            product.getId(),
            product.getProductName(),
            imageUrl, // URL hình ảnh
            product.getDescription(),
            price, // Giá sản phẩm
            categoryName, // Tên danh mục
            totalQuantity, // Tổng số lượng từ inbounds
            product.getSlug() // Slug cho sản phẩm
        );
    }
    
}
