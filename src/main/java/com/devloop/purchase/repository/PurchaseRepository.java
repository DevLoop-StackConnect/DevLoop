package com.devloop.purchase.repository;

import com.devloop.purchase.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    boolean existsByUserIdAndProductId(Long userId, Long productId);
}
