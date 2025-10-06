package com.monthly.ecommercemonolith.controllers;


import com.monthly.ecommercemonolith.config.AppConstants;
import com.monthly.ecommercemonolith.payload.ProductDTO;
import com.monthly.ecommercemonolith.payload.ProductResponse;
import com.monthly.ecommercemonolith.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Tag(name = "Products", description = "Managing products controller")
    @Operation(summary = "Create product", description = "Api to create " +
            "products")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Category " +
            "is created successfully."), @ApiResponse(responseCode = "400",
            description = "Invalid input", content = @Content)})
    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productDTO
            , @PathVariable Long categoryId) {
        ProductDTO savedProduct = productService.addProduct(productDTO, categoryId);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @Tag(name = "Products", description = "Managing products controller")
    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(@RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                          @RequestParam(name
                                                                  =
                                                                  "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                          @RequestParam(name
                                                                  = "sortBy",
                                                                  defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
                                                          @RequestParam(name
                                                                  = "sortOrder",
                                                                  defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder
    ) {
        ProductResponse products = productService.getAllProducts(pageSize,
                pageNumber, sortBy, sortOrder);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Tag(name = "Products", description = "Managing products controller")
    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(@Parameter(description = "Product you wish to get") @PathVariable Long categoryId, @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                 @RequestParam(name
                                                                         =
                                                                         "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                 @RequestParam(name
                                                                         = "sortBy",
                                                                         defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
                                                                 @RequestParam(name
                                                                         = "sortOrder",
                                                                         defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {
        ProductResponse products = productService.searchByCategory(categoryId, pageSize,
                pageNumber, sortBy, sortOrder);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Tag(name = "Products", description = "Managing products controller")
    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String keyword, @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                @RequestParam(name
                                                                        =
                                                                        "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                @RequestParam(name
                                                                        = "sortBy",
                                                                        defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
                                                                @RequestParam(name
                                                                        = "sortOrder",
                                                                        defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {
        ProductResponse products = productService.searchByKeyword(keyword, pageSize,
                pageNumber, sortBy, sortOrder);
        return new ResponseEntity<>(products, HttpStatus.FOUND);
    }

    @Tag(name = "Products", description = "Managing products controller")
    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long productId,
                                                    @Valid @RequestBody ProductDTO productDTO) {
        ProductDTO updatedProductDTO = productService.updateProduct(productId,
                productDTO);
        return new ResponseEntity<>(updatedProductDTO, HttpStatus.OK);
    }

    @Tag(name = "Products", description = "Managing products controller")
    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId) {
        ProductDTO productDTO = productService.deleteProduct(productId);
        return new ResponseEntity<>(productDTO, HttpStatus.NO_CONTENT);
    }

    @Tag(name = "Products", description = "Managing products controller")
    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId, @RequestParam("image") MultipartFile image) throws IOException {
        ProductDTO productDTO = productService.updateProductImage(productId,
                image);
        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }
}
