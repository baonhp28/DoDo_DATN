package com.poly.datn.dto;





import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Integer id;
   
    private String productName;
   
    private String imageUrl;
    private String description;
    private double price;
    private String categoryName;
     private int quantity;  // Added field f
     private String slug; // Thêm thuộc tính slug
}
