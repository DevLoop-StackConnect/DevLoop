package com.devloop.cart.repository;

import com.devloop.cart.entity.CartItem;
import com.devloop.cart.response.CartItemListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("SELECT new com.devloop.cart.response.CartItemListResponse(c.product.title, c.product.price) FROM CartItem c " +
            "WHERE c.cart.id = :cartId " +
            "ORDER BY c.createdAt ASC")
    Page<CartItemListResponse> findAllByCartId(Pageable pageable, @Param("cartId") Long cartId);
}
