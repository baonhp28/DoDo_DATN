// // package com.poly.datn.service;

// // import org.springframework.beans.factory.annotation.Autowired;
// // import org.springframework.stereotype.Service;

// // import com.poly.datn.DTO.ProductDTO;
// // import com.poly.datn.entities.Products;
// // import com.poly.datn.entities.ProductImage;
// // import com.poly.datn.entities.Skus;
// // import com.poly.datn.mapper.ProductMapper;
// // import com.poly.datn.repository.ProductsRepository;
// // import com.poly.datn.repository.ProductImageRepository;
// // import com.poly.datn.repository.SkusRepository;

// // import java.util.List;
// // import java.util.Optional;
// // import java.util.stream.Collectors;

// // @Service
// // public class ProductsService {

// //     @Autowired
// //     private ProductsRepository productsRepository;

// //     @Autowired
// //     private ProductImageRepository productImageRepository;

// //     @Autowired
// //     private SkusRepository skusRepository;

// //     public List<ProductDTO> getAllProducts() {
// //         return productsRepository.findAll().stream()
// //                 .map(this::mapToProductDTO)
// //                 .collect(Collectors.toList());
// //     }

// //     public Optional<ProductDTO> getProductById(Integer id) {
// //         return productsRepository.findById(id)
// //                 .map(this::mapToProductDTO);
// //     }

// //     public ProductDTO createProduct(Products product) {
// //         Products savedProduct = productsRepository.save(product);
// //         return mapToProductDTO(savedProduct);
// //     }

// //     public ProductDTO updateProduct(Integer id, Products productDetails) {
// //         Optional<Products> optionalProduct = productsRepository.findById(id);
// //         if (optionalProduct.isPresent()) {
// //             Products product = optionalProduct.get();
// //             product.setProductName(productDetails.getProductName());
// //             product.setSlug(productDetails.getSlug());
// //             product.setDescription(productDetails.getDescription());
// //             product.setCategory(productDetails.getCategory());
// //             Products updatedProduct = productsRepository.save(product);
// //             return mapToProductDTO(updatedProduct);
// //         } else {
// //             throw new RuntimeException("Product not found with id " + id);
// //         }
// //     }

// //     public void deleteProduct(Integer id) {
// //         productsRepository.deleteById(id);
// //     }

// //     private ProductDTO mapToProductDTO(Products product) {
// //         ProductImage primaryImage = productImageRepository.findPrimaryImageByProductId(product.getId());
// //         Skus sku = skusRepository.findSkuByProductId(product.getId());
// //         return ProductMapper.toProductDTO(product, primaryImage, sku);
// //     }
// // }
// // package com.poly.datn.service;

// // import org.springframework.beans.factory.annotation.Autowired;
// // import org.springframework.stereotype.Service;

// // import com.poly.datn.DTO.ProductDTO;
// // import com.poly.datn.entities.Products;
// // import com.poly.datn.entities.ProductImage;
// // import com.poly.datn.entities.Skus;
// // import com.poly.datn.entities.Category;
// // import com.poly.datn.entities.Inbound;
// // import com.poly.datn.mapper.ProductMapper;
// // import com.poly.datn.repository.ProductsRepository;
// // import com.poly.datn.repository.ProductImageRepository;
// // import com.poly.datn.repository.SkusRepository;
// // import com.poly.datn.repository.CategoryRepository;
// // import com.poly.datn.repository.InboundRepository;

// // import java.util.List;
// // import java.util.Optional;
// // import java.util.stream.Collectors;

// // @Service
// // public class ProductsService {

// //     @Autowired
// //     private ProductsRepository productsRepository;

// //     @Autowired
// //     private ProductImageRepository productImageRepository;

// //     @Autowired
// //     private SkusRepository skusRepository;

// //     @Autowired
// //     private InboundRepository inboundRepository; // Thêm repository để truy xuất dữ liệu Inbound
// //  @Autowired
// //     private CategoryRepository categoryRepository; // Thêm repository để tìm danh mục
    
// //     public List<ProductDTO> getAllProducts() {
// //         return productsRepository.findAll().stream()
// //                 .map(this::mapToProductDTO)
// //                 .collect(Collectors.toList());
// //     }

// //     public Optional<ProductDTO> getProductById(Integer id) {
// //         return productsRepository.findById(id)
// //                 .map(this::mapToProductDTO);
// //     }

// //     public ProductDTO createProduct(ProductDTO request) {
// //         Category category = categoryRepository.findByCategoryName(request.getCategoryName())
// //                 .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại với tên " + request.getCategoryName()));

// //         Products product = new Products();
// //         product.setProductName(request.getProductName());
// //         product.setDescription(request.getDescription());
// //         product.setCategory(category);
        
// //         Products savedProduct = productsRepository.save(product);

// //         // Lưu hình ảnh và SKU nếu cần
// //         ProductImage productImage = new ProductImage();
// //         productImage.setImageUrl(request.getImageUrl());
// //         productImage.setProduct(savedProduct);
// //         productImageRepository.save(productImage);

// //         Skus sku = new Skus();
// //         sku.setProduct(savedProduct);
// //         sku.setPrice(request.getPrice());
// //         // Thiết lập số lượng hoặc các trường cần thiết khác
// //         skusRepository.save(sku);

// //         // Xử lý inbound nếu cần

// //         return mapToProductDTO(savedProduct);
// //     }

// //     public ProductDTO updateProduct(Integer id, ProductDTO productDetails) {
// //         Optional<Products> optionalProduct = productsRepository.findById(id);
// //         if (optionalProduct.isPresent()) {
// //             Products product = optionalProduct.get();
// //             product.setProductName(productDetails.getProductName());
// //             product.setDescription(productDetails.getDescription());
// //             // Không còn sử dụng slug nữa
// //             Category category = categoryRepository.findByCategoryName(productDetails.getCategoryName())
// //                     .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại với tên " + productDetails.getCategoryName()));
// //             product.setCategory(category);
// //             Products updatedProduct = productsRepository.save(product);
// //             return mapToProductDTO(updatedProduct);
// //         } else {
// //             throw new RuntimeException("Sản phẩm không tồn tại với ID " + id);
// //         }
// //     }


// //     public void deleteProduct(Integer id) {
// //         productsRepository.deleteById(id);
// //     }

// //     private ProductDTO mapToProductDTO(Products product) {
// //         ProductImage primaryImage = productImageRepository.findPrimaryImageByProductId(product.getId());
// //         Skus sku = skusRepository.findSkuByProductId(product.getId());
// //         List<Inbound> inbounds = inboundRepository.findBySku(sku);
// //         return ProductMapper.toProductDTO(product, primaryImage, sku, inbounds);
// //     }
// // }
//  package com.poly.datn.service;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import com.poly.datn.DTO.ProductDTO;
// import com.poly.datn.entities.Products;
// import com.poly.datn.entities.ProductImage;
// import com.poly.datn.entities.Skus;
// import com.poly.datn.entities.Category;
// import com.poly.datn.entities.Inbound;
// import com.poly.datn.mapper.ProductMapper;
// import com.poly.datn.repository.ProductsRepository;
// import com.poly.datn.repository.ProductImageRepository;
// import com.poly.datn.repository.SkusRepository;
// import com.poly.datn.repository.CategoryRepository;
// import com.poly.datn.repository.InboundRepository;


// import java.text.Normalizer;
// import java.util.List;
// import java.util.Optional;
// import java.util.stream.Collectors;

// @Service
// public class ProductsService {

//     @Autowired
//     private ProductsRepository productsRepository;

//     @Autowired
//     private ProductImageRepository productImageRepository;

//     @Autowired
//     private SkusRepository skusRepository;

//     @Autowired
//     private InboundRepository inboundRepository;

//     @Autowired
//     private CategoryRepository categoryRepository;

//     public List<ProductDTO> getAllProducts() {
//         return productsRepository.findAll().stream()
//                 .map(this::mapToProductDTO)
//                 .collect(Collectors.toList());
//     }

//     public Optional<ProductDTO> getProductById(Integer id) {
//         return productsRepository.findById(id)
//                 .map(this::mapToProductDTO);
//     }

//     public ProductDTO createProduct(ProductDTO request) {
//         Category category = categoryRepository.findByCategoryName(request.getCategoryName())
//                 .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại với tên " + request.getCategoryName()));

//         Products product = new Products();
//         product.setProductName(request.getProductName());
//         product.setDescription(request.getDescription());
//         product.setCategory(category);
//         product.setSlug(generateSlug(request.getProductName())); // Tạo slug

//         Products savedProduct = productsRepository.save(product);

//         // Lưu hình ảnh và SKU nếu cần
//         if (request.getImageUrl() != null && !request.getImageUrl().isEmpty()) {
//             ProductImage productImage = new ProductImage();
//             productImage.setImageUrl(request.getImageUrl());
//             productImage.setProduct(savedProduct);
//             productImageRepository.save(productImage);
//         }

//         Skus sku = new Skus();
//         sku.setProduct(savedProduct);
//         sku.setPrice(request.getPrice());
//         // Thiết lập số lượng hoặc các trường cần thiết khác
//         skusRepository.save(sku);

//         return mapToProductDTO(savedProduct);
//     }

//     public ProductDTO updateProduct(Integer id, ProductDTO productDetails) {
//         Optional<Products> optionalProduct = productsRepository.findById(id);
//         if (optionalProduct.isPresent()) {
//             Products product = optionalProduct.get();
//             product.setProductName(productDetails.getProductName());
//             product.setDescription(productDetails.getDescription());
//             product.setSlug(generateSlug(productDetails.getProductName())); // Tạo slug mới nếu cần

//             Category category = categoryRepository.findByCategoryName(productDetails.getCategoryName())
//                     .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại với tên " + productDetails.getCategoryName()));
//             product.setCategory(category);
//             Products updatedProduct = productsRepository.save(product);
//             return mapToProductDTO(updatedProduct);
//         } else {
//             throw new RuntimeException("Sản phẩm không tồn tại với ID " + id);
//         }
//     }

//     private String generateSlug(String productName) {
//         String slug = Normalizer.normalize(productName, Normalizer.Form.NFD)
//                                 .replaceAll("[^\\w\\s-]", "")
//                                 .replaceAll("\\s+", "-")
//                                 .toLowerCase();
//         return slug;
//     }

//     private ProductDTO mapToProductDTO(Products product) {
//         ProductImage primaryImage = productImageRepository.findPrimaryImageByProductId(product.getId());
//         Skus sku = skusRepository.findSkuByProductId(product.getId());
//         List<Inbound> inbounds = inboundRepository.findBySku(sku);
//         return ProductMapper.toProductDTO(product, primaryImage, sku, inbounds);
//     }
//         public void deleteProduct(Integer id) {
//         productsRepository.deleteById(id);
//     }

// }
package com.poly.datn.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.poly.datn.dto.ProductDTO;
import com.poly.datn.entities.Products;
import com.poly.datn.entities.ProductImage;
import com.poly.datn.entities.Skus;
import com.poly.datn.entities.Category;
import com.poly.datn.entities.Inbound;
import com.poly.datn.mapper.ProductMapper;
import com.poly.datn.repository.ProductsRepository;
import com.poly.datn.repository.ProductImageRepository;
import com.poly.datn.repository.SkusRepository;
import com.poly.datn.repository.CategoryRepository;
import com.poly.datn.repository.InboundRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductsService {

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private SkusRepository skusRepository;

    @Autowired
    private InboundRepository inboundRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private final String uploadDir = "path/to/upload/dir"; // Change this to your actual upload directory

    public List<ProductDTO> getAllProducts() {
        return productsRepository.findAll().stream()
                .map(this::mapToProductDTO)
                .collect(Collectors.toList());
    }

    public Optional<ProductDTO> getProductById(Integer id) {
        return productsRepository.findById(id)
                .map(this::mapToProductDTO);
    }

    public ProductDTO createProduct(ProductDTO request, MultipartFile imageFile) throws IOException {
        // Kiểm tra xem sản phẩm với tên đã cho có tồn tại không
        if (productsRepository.existsByProductName(request.getProductName())) {
            throw new RuntimeException("Sản phẩm với tên '" + request.getProductName() + "' đã tồn tại.");
        }
    
        // Tìm danh mục theo tên
        Category category = categoryRepository.findByCategoryName(request.getCategoryName())
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại với tên " + request.getCategoryName()));
    
        // Tạo mới đối tượng Products
        Products product = new Products();
        product.setProductName(request.getProductName());
        product.setDescription(request.getDescription());
        product.setCategory(category);
        product.setSlug(generateSlug(request.getProductName())); // Tạo slug
    
        // Lưu sản phẩm vào cơ sở dữ liệu
        Products savedProduct = productsRepository.save(product);
    
        // Lưu hình ảnh nếu có
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = saveImage(imageFile);
            ProductImage productImage = new ProductImage();
            productImage.setImageUrl(imageUrl);
            productImage.setProduct(savedProduct);
            productImageRepository.save(productImage);
        }
    
        // Tạo và lưu SKU
        Skus sku = new Skus();
        sku.setProduct(savedProduct);
        sku.setPrice(request.getPrice());
        // Thiết lập các trường cần thiết khác nếu có
        skusRepository.save(sku);
    
        return mapToProductDTO(savedProduct);
    }
    

    public ProductDTO updateProduct(Integer id, ProductDTO productDetails) {
        Optional<Products> optionalProduct = productsRepository.findById(id);
        if (optionalProduct.isPresent()) {
            Products product = optionalProduct.get();
            product.setProductName(productDetails.getProductName());
            product.setDescription(productDetails.getDescription());
            product.setSlug(generateSlug(productDetails.getProductName())); // Tạo slug mới nếu cần

            Category category = categoryRepository.findByCategoryName(productDetails.getCategoryName())
                    .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại với tên " + productDetails.getCategoryName()));
            product.setCategory(category);
            Products updatedProduct = productsRepository.save(product);
            return mapToProductDTO(updatedProduct);
        } else {
            throw new RuntimeException("Sản phẩm không tồn tại với ID " + id);
        }
    }

    public void deleteProduct(Integer id) {
        productsRepository.deleteById(id);
    }

    private String generateSlug(String productName) {
        String slug = Normalizer.normalize(productName, Normalizer.Form.NFD)
                                .replaceAll("[^\\w\\s-]", "")
                                .replaceAll("\\s+", "-")
                                .toLowerCase();
        return slug;
    }

    private String saveImage(MultipartFile img) throws IOException {
        // Lấy tên file gốc
        String fileName = System.currentTimeMillis() + "_" + img.getOriginalFilename();

        // Kiểm tra xem file có định dạng hợp lệ không
        if (fileName == null || (!fileName.endsWith(".jpg") && !fileName.endsWith(".jpeg") && !fileName.endsWith(".png"))) {
            throw new IOException("Định dạng file không hợp lệ. Vui lòng chọn file có định dạng .jpg, .jpeg hoặc .png.");
        }

        // Đảm bảo rằng thư mục tải lên tồn tại
        Files.createDirectories(Paths.get(uploadDir));

        File uploadFile = new File(uploadDir + File.separator + fileName);

        try {
            // Lưu file vào thư mục tải lên
            img.transferTo(uploadFile);
        } catch (IOException e) {
            // Ném ra lỗi nếu có sự cố khi lưu file
            throw new IOException("Lỗi khi lưu file hình ảnh", e);
        }

        // Trả về tên file đã lưu
        return fileName;
    }

    private ProductDTO mapToProductDTO(Products product) {
        ProductImage primaryImage = productImageRepository.findPrimaryImageByProductId(product.getId());
        Skus sku = skusRepository.findSkuByProduct_Id(product.getId());
        List<Inbound> inbounds = inboundRepository.findBySku(sku);
        return ProductMapper.toProductDTO(product, primaryImage, sku, inbounds);
    }
    
}
