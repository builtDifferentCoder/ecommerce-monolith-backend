package com.monthly.ecommercemonolith.serviceImplementations;


import com.monthly.ecommercemonolith.entities.Cart;
import com.monthly.ecommercemonolith.entities.Category;
import com.monthly.ecommercemonolith.entities.Product;
import com.monthly.ecommercemonolith.exceptions.APIException;
import com.monthly.ecommercemonolith.exceptions.ResourceNotFoundException;
import com.monthly.ecommercemonolith.payload.CartDTO;
import com.monthly.ecommercemonolith.payload.ProductDTO;
import com.monthly.ecommercemonolith.payload.ProductResponse;
import com.monthly.ecommercemonolith.repositories.CartRepository;
import com.monthly.ecommercemonolith.repositories.CategoryRepository;
import com.monthly.ecommercemonolith.repositories.ProductRepository;
import com.monthly.ecommercemonolith.services.CartService;
import com.monthly.ecommercemonolith.services.FileService;
import com.monthly.ecommercemonolith.services.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final ModelMapper modelMapper;
    private final FileService fileService;
    @Value("${product.images}")
    private String path;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, CartRepository cartRepository, CartService cartService, ModelMapper modelMapper, FileService fileService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.cartRepository = cartRepository;
        this.cartService = cartService;
        this.modelMapper = modelMapper;
        this.fileService = fileService;
    }

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new ResourceNotFoundException("Category Id", categoryId,
                        "Category")
        );
        boolean ifProductIsNotPresent = true;
        List<Product> products = category.getProductList();
        for (Product value : products) {
            if (value.getProductName().equals(productDTO.getProductName())) {
                ifProductIsNotPresent = false;
                break;
            }
        }
        if (ifProductIsNotPresent) {
            Product product = modelMapper.map(productDTO, Product.class);
            product.setCategory(category);
            product.setImage("default.png");
            double specialPrice =
                    product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
            product.setSpecialPrice(specialPrice);
            Product savedProduct = productRepository.save(product);
            return modelMapper.map(savedProduct,
                    ProductDTO.class);
        } else {
            throw new APIException("Product already exists.");
        }
    }

    @Override
    public ProductResponse getAllProducts(Integer pageSize, Integer pageNumber
            , String sortBy, String sortOrder) {
        Sort sortByAndSortOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndSortOrder);
        Page<Product> productPage = productRepository.findAll(pageDetails);
        List<Product> products = productPage.getContent();
        if (products.isEmpty()) throw new APIException("No products exists");
        List<ProductDTO> productDTOS =
                products.stream().map(product -> modelMapper.map(product,
                        ProductDTO.class)).toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageSize(productPage.getSize());
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageSize, Integer pageNumber
            , String sortBy, String sortOrder) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new ResourceNotFoundException("Category Id", categoryId,
                        "Category")
        );
        Sort sortByAndSortOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndSortOrder);
        Page<Product> productPage =
                productRepository.findByCategoryOrderByPriceAsc(category, pageDetails);
        List<Product> products = productPage.getContent();
        if (products.isEmpty()) throw new APIException("No products exists");
        List<ProductDTO> productDTOS =
                products.stream().map(product -> modelMapper.map(product,
                        ProductDTO.class)).toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageSize(productPage.getSize());
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse searchByKeyword(String keyword, Integer pageSize, Integer pageNumber
            , String sortBy, String sortOrder) {
        Sort sortByAndSortOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndSortOrder);
        Page<Product> productPage =
                productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%', pageDetails);
        List<Product> products = productPage.getContent();
        if (products.isEmpty()) throw new APIException("No products exists");
        List<ProductDTO> productDTOS =
                products.stream().map(product -> modelMapper.map(product,
                        ProductDTO.class)).toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageSize(productPage.getSize());
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product existingProduct =
                productRepository.findById(productId).orElseThrow(
                        () -> new ResourceNotFoundException("Product Id", productId,
                                "Product")
                );

        Product product = modelMapper.map(productDTO, Product.class);
        existingProduct.setProductName(product.getProductName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setDiscount(product.getDiscount());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setSpecialPrice(product.getSpecialPrice());
        Product updatedProduct = productRepository.save(existingProduct);
        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        List<CartDTO> cartDTOS = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
            List<ProductDTO> productDTOS =
                    cart.getCartItems().stream().map(p -> modelMapper.map(p.getProduct(), ProductDTO.class)).toList();
            cartDTO.setProducts(productDTOS);
            return cartDTO;
        }).toList();
        cartDTOS.forEach(cart -> cartService.updateProductInCarts(cart.getCartId(), productId));

        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("Product Id", productId,
                        "Product")
        );
        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(), productId));
        productRepository.delete(product);
        return modelMapper.map(product, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        //get product from db
        //upload image to server
        //get the file name of uploaded image
        // updating the file name of the product
        //return the product dto
        Product existingProduct = productRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("Product Id", productId,
                        "Product")
        );
        String fileName = fileService.uploadImage(path, image);

        existingProduct.setImage(fileName);
        Product savedProduct = productRepository.save(existingProduct);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }


}
