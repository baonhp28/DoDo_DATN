// package com.poly.datn.controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import com.poly.datn.DTO.ProductDTO;
// import com.poly.datn.service.ProductsService;

// import java.util.List;

// @RestController
// @RequestMapping("/api/products")
// public class ProductsController {

//     @Autowired
//     private ProductsService productsService;

//     @GetMapping
//     public ResponseEntity<List<ProductDTO>> getAllProducts() {
//         List<ProductDTO> products = productsService.getAllProducts();
//         return ResponseEntity.ok(products);
//     }

//     @GetMapping("/{id}")
//     public ResponseEntity<ProductDTO> getProductById(@PathVariable Integer id) {
//         return productsService.getProductById(id)
//                 .map(productDTO -> ResponseEntity.ok(productDTO))
//                 .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
//                         .body(null));
//     }

//     @PostMapping
//     public ResponseEntity<String> createProduct(@RequestBody ProductDTO request) {
//         try {
//             ProductDTO createdProduct = productsService.createProduct(request);
//             return ResponseEntity.status(HttpStatus.CREATED)
//                     .body("Sản phẩm đã được tạo thành công với ID: " + createdProduct.getId());
//         } catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                     .body("Tạo sản phẩm không thành công: " + e.getMessage());
//         }
//     }
    

  
//     @PutMapping("/{id}")
//     public ResponseEntity<String> updateProduct(@PathVariable Integer id, @RequestBody ProductDTO productDetails) {
//         try {
//             // Cập nhật sản phẩm và trả về thông báo thành công
//             ProductDTO updatedProduct = productsService.updateProduct(id, productDetails);
//             return ResponseEntity.ok("Sản phẩm đã được cập nhật thành công với ID: " + updatedProduct.getId());
//         } catch (RuntimeException e) {
//             // Trả về thông báo lỗi nếu không tìm thấy sản phẩm
//             return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                     .body("Không tìm thấy sản phẩm với ID: " + id);
//         } catch (Exception e) {
//             // Xử lý các lỗi khác nếu có
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                     .body("Cập nhật sản phẩm không thành công: " + e.getMessage());
//         }
//     }


//     @DeleteMapping("/{id}")
//     public ResponseEntity<String> deleteProduct(@PathVariable Integer id) {
//         try {
//             productsService.deleteProduct(id);
//             return ResponseEntity.status(HttpStatus.OK)
//                     .body("Sản phẩm đã được xóa thành công với ID: " + id);
//         } catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                     .body("Xóa sản phẩm không thành công: " + e.getMessage());
//         }
//     }
    
// }
 package com.poly.datn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.poly.datn.dto.ProductDTO;
import com.poly.datn.service.ProductsService;


import java.util.List;


@RestController
@RequestMapping("/api/products")
public class ProductsController {

    @Autowired
    private ProductsService productsService;

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        try {
            List<ProductDTO> products = productsService.getAllProducts();
            if (products.isEmpty()) {
                return ResponseEntity.noContent().build(); // Returns 204 No Content if no products are found
            }
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            // Log the exception (consider using a logger here)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Integer id) {
        try {
            return productsService.getProductById(id)
                    .map(productDTO -> ResponseEntity.ok(productDTO))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(null));
        } catch (Exception e) {
            // Log the exception (consider using a logger here)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PostMapping
    public ResponseEntity<String> createProduct(@RequestBody ProductDTO request, MultipartFile imageFile) {
        try {
            ProductDTO createdProduct = productsService.createProduct(request,  imageFile);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Sản phẩm đã được tạo thành công với ID: " + createdProduct.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Tạo sản phẩm không thành công: " + e.getMessage());
        } catch (Exception e) {
            // Log the exception (consider using a logger here)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Tạo sản phẩm không thành công: " + e.getMessage());
        }
        
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable Integer id, @RequestBody ProductDTO productDetails) {
        try {
            ProductDTO updatedProduct = productsService.updateProduct(id, productDetails);
            return ResponseEntity.ok("Sản phẩm đã được cập nhật thành công với ID: " + updatedProduct.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy sản phẩm với ID: " + id);
        } catch (Exception e) {
            // Log the exception (consider using a logger here)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Cập nhật sản phẩm không thành công: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Integer id) {
        try {
            // Check if the product exists before attempting to delete
            boolean exists = productsService.getProductById(id).isPresent();
            if (!exists) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy sản phẩm với ID: " + id);
            }
            
            productsService.deleteProduct(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Sản phẩm đã được xóa thành công với ID: " + id);
        } catch (Exception e) {
            // Log the exception (consider using a logger here)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Xóa sản phẩm không thành công: " + e.getMessage());
        }
    }
    
}
