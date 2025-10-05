package com.monthly.ecommercemonolith.services;

import com.monthly.ecommercemonolith.payload.ProductDTO;
import com.monthly.ecommercemonolith.payload.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductDTO addProduct(ProductDTO product, Long categoryId);

    ProductResponse getAllProducts(Integer pageSize, Integer pageNumber,
                                   String sortBy, String sortOrder);

    ProductResponse searchByCategory(Long categoryId, Integer pageSize, Integer pageNumber,
                                     String sortBy, String sortOrder);

    ProductResponse searchByKeyword(String keyword, Integer pageSize, Integer pageNumber,
                                    String sortBy, String sortOrder);

    ProductDTO updateProduct(Long productId, ProductDTO product);

    ProductDTO deleteProduct(Long productId);

    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;
}
