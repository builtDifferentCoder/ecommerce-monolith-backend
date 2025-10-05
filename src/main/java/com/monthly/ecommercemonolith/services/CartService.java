package com.monthly.ecommercemonolith.services;

import com.monthly.ecommercemonolith.payload.CartDTO;
import jakarta.transaction.Transactional;

import java.util.List;

public interface CartService {
    CartDTO addProductToCart(Long productId,Integer quantity);
    List<CartDTO> getAllCarts();

    CartDTO getUserCart(String emailId, Long cartId);

    @Transactional
    CartDTO updateProductQuantity(Long productId, Integer quantity);

    String deleteProductFromCart(Long cartId, Long productId);

    void updateProductInCarts(Long cartId, Long productId);
}
