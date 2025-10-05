package com.monthly.ecommercemonolith.repositories;

import com.monthly.ecommercemonolith.entities.CartItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("SELECT ci from CartItem  ci where ci.cart.cartId=?1 and ci.product.id=?2")
    CartItem findCartItemByProductIdAndCartId(Long cartId, Long productId);

    @Transactional
    @Modifying
    @Query("delete from CartItem ci where ci.cart.cartId=?1 and ci.product.id=?2")
    void deleteCartItemByProductIdAndCartId(Long cartId, Long productId);
}
